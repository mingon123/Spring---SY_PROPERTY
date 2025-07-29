package kr.spring.notice.controller;

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
import kr.spring.notice.service.NoticeService;
import kr.spring.notice.vo.NoticeVO;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/notice")
public class NoticeAdminController {

    @Autowired
    private NoticeService noticeService;

    // 자바빈(VO) 초기화
    @ModelAttribute
    public NoticeVO initCommand() {
        return new NoticeVO();
    }

    // 글쓰기 폼
    @GetMapping("/write")
    public String form() {
        return "views/notice/noticeWrite";
    }

    // 글쓰기 처리
    @PostMapping("/write")
    public String submit(@Valid NoticeVO noticeVO,
                         BindingResult result,
                         HttpServletRequest request,
                         Model model) throws IllegalStateException, IOException {

        log.debug("<<게시판 글등록>> : {}", noticeVO);

        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            return form();
        }

        noticeService.insertNotice(noticeVO);

        model.addAttribute("message", "글을 정상적으로 등록했습니다.");
        model.addAttribute("url", request.getContextPath() + "/admin/notice/list");

        return "views/common/resultAlert";
    }

    // 공지사항 목록
    @GetMapping("/list")
    public String getList(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                          Model model) {
        int count = noticeService.selectRowCount(new HashMap<>());

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        List<NoticeVO> list = null;
        if (count > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());
            list = noticeService.selectList(map);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/notice/noticeList";
    }

    // 공지사항 상세
    @GetMapping("/detail")
    public String getDetail(@RequestParam("notice_num") long notice_num,
                            Model model) {

        log.debug("<<게시판 상세>> notice_num : {}", notice_num);

        NoticeVO notice = noticeService.selectNotice(notice_num);
        notice.setTitle(StringUtil.useNoHtml(notice.getTitle()));

        model.addAttribute("notice", notice);

        return "views/notice/noticeView";
    }

    // 공지사항 수정 폼
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/update")
    public String formUpdate(@RequestParam("notice_num") long notice_num,
                             Model model) {

        NoticeVO noticeVO = noticeService.selectNotice(notice_num);
        model.addAttribute("noticeVO", noticeVO);

        return "views/notice/noticeModify";
    }

    // 공지사항 수정 처리
   //@PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String submitUpdate(@Valid NoticeVO noticeVO,
                                BindingResult result,
                                HttpServletRequest request,
                                Model model) throws IllegalStateException, IOException {

        log.debug("<<글 수정>> : {}", noticeVO);

        // 유효성 검사
        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            return "views/notice/noticeModify";
        }

        noticeService.updateNotice(noticeVO);

        model.addAttribute("message", "글 수정 완료!");
        model.addAttribute("url", request.getContextPath() + "/admin/notice/detail?notice_num=" + noticeVO.getNotice_num());

        return "views/common/resultAlert";
    }

    // 공지사항 삭제
   //@PreAuthorize("isAuthenticated()")
    @GetMapping("/delete")
    public String submitDelete(@RequestParam("notice_num") long notice_num,
                                HttpServletRequest request) {

        log.debug("<<게시글 삭제>> notice_num : {}", notice_num);

        noticeService.deleteNotice(notice_num);

        return "redirect:/admin/notice/list";
    }
}
