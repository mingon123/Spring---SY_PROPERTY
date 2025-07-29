package kr.spring.tip.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import kr.spring.tip.service.TipService;
import kr.spring.tip.vo.TipVO;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/tip")
public class TipAdminController {
    @Autowired
    private TipService tipService;

    // 자바빈(VO) 초기화
    @ModelAttribute
    public TipVO initCommand() {
        return new TipVO();
    }

    // 글쓰기 폼
    @GetMapping("/write")
    public String form() {
        return "views/tip/tipWrite";
    }

    // 글쓰기 처리
    @PostMapping("/write")
    public String submit(@Valid TipVO tipVO,
                         BindingResult result,
                         HttpServletRequest request,
                         Model model) throws IllegalStateException, IOException {

        log.debug("<<tip 글등록>> : {}", tipVO);

        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            return form();
        }
        
        // 파일 업로드 처리
        tipVO.setPhoto_name(FileUtil.createFile(request, tipVO.getUpload()));
        tipService.insertTip(tipVO);

        model.addAttribute("message", "글을 정상적으로 등록했습니다.");
        model.addAttribute("url", request.getContextPath() + "/admin/tip/list");

        return "views/common/resultAlert";
    }

    // tip 목록
    @GetMapping("/list")
    public String getList(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                          Model model) {
        int count = tipService.selectRowCount(new HashMap<>());

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        List<TipVO> list = null;
        if (count > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());
            list = tipService.selectList(map);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/tip/tipList";
    }

    // tip 상세
    @GetMapping("/detail")
    public String getDetail(@RequestParam("tip_num") long tip_num,
                            Model model) {

        log.debug("<<tip 상세>> tip_num : {}", tip_num);

        TipVO tip = tipService.selectTip(tip_num);
        tip.setTitle(StringUtil.useNoHtml(tip.getTitle()));

        model.addAttribute("tip", tip);

        return "views/tip/tipView";
    }

    // tip 수정 폼
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/update")
    public String formUpdate(@RequestParam("tip_num") long tip_num,
                             Model model) {

        TipVO tipVO = tipService.selectTip(tip_num);
        model.addAttribute("tipVO", tipVO);

        return "views/tip/tipModify";
    }

    // tip 수정 처리
    //@PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String submitUpdate(@Valid TipVO tipVO,
                               BindingResult result,
                               HttpServletRequest request,
                               Model model) throws IllegalStateException, IOException {

        log.debug("<<글 수정>> : {}", tipVO);
        TipVO db_tip = tipService.selectTip(tipVO.getTip_num());

        // 유효성 검사
        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            tipVO.setPhoto_name(db_tip.getPhoto_name());
            return "views/tip/tipModify";
        }
        
        // 파일명 셋팅
        tipVO.setPhoto_name(FileUtil.createFile(request, tipVO.getUpload()));

        tipService.updateTip(tipVO);

        if (tipVO.getUpload() != null && !tipVO.getUpload().isEmpty()) {
            // 수정 전 파일 삭제 처리
            FileUtil.removeFile(request, db_tip.getPhoto_name());
        }

        model.addAttribute("message", "글 수정 완료!");
        model.addAttribute("url", request.getContextPath() + "/admin/tip/detail?tip_num=" + tipVO.getTip_num());

        return "views/common/resultAlert";
    }

    // tip 삭제
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/delete")
    public String submitDelete(@RequestParam("tip_num") long tip_num,
                               HttpServletRequest request) {

        log.debug("<<tip 삭제>> tip_num : {}", tip_num);

        TipVO db_board = tipService.selectTip(tip_num);

        tipService.deleteTip(tip_num);

        if (db_board.getPhoto_name() != null) {
            // 파일 삭제
            FileUtil.removeFile(request, db_board.getPhoto_name());
        }

        return "redirect:/admin/tip/list";
    }
}
