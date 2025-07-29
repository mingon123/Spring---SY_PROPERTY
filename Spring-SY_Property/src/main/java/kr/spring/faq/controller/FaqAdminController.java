package kr.spring.faq.controller;

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
import kr.spring.faq.service.FaqService;
import kr.spring.faq.vo.FaqVO;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/faq")
public class FaqAdminController {

    @Autowired
    private FaqService faqService;

    // 자바빈(VO) 초기화
    @ModelAttribute
    public FaqVO initCommand() {
        return new FaqVO();
    }

    // 글쓰기 폼
    @GetMapping("/write")
    public String form() {
        return "views/faq/faqWrite";  // 경로 소문자 통일 권장
    }

    // 글쓰기 처리
    @PostMapping("/write")
    public String submit(@Valid FaqVO faqVO,
                         BindingResult result,
                         HttpServletRequest request,
                         Model model) throws IllegalStateException, IOException {

        log.debug("<<게시판 글등록>> : {}", faqVO);

        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            return form();
        }

        faqService.insertFaq(faqVO);

        model.addAttribute("message", "글을 정상적으로 등록했습니다.");
        // 리다이렉트 경로 수정
        model.addAttribute("url", request.getContextPath() + "/admin/faq/list");

        return "views/common/resultAlert";
    }

    // 공지사항 목록
    @GetMapping("/list")
    public String getList(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                          Model model) {
        int count = faqService.selectRowCount(new HashMap<>());

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        List<FaqVO> list = null;
        if (count > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());
            list = faqService.selectList(map);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/faq/faqList";
    }

    // 공지사항 상세
    @GetMapping("/detail")
    public String getDetail(@RequestParam("faq_num") long faq_num,
                            Model model) {

        log.debug("<<게시판 상세>> faq_num : {}", faq_num);

        FaqVO faq = faqService.selectFaq(faq_num);
        faq.setTitle(StringUtil.useNoHtml(faq.getTitle()));

        model.addAttribute("faq", faq);

        return "views/faq/faqView";
    }

    // 공지사항 수정 폼
    @GetMapping("/update")
    public String formUpdate(@RequestParam("faq_num") long faq_num,
                             Model model) {

        FaqVO faqVO = faqService.selectFaq(faq_num);
        model.addAttribute("faqVO", faqVO);

        return "views/faq/faqModify";
    }

    // 공지사항 수정 처리
    @PostMapping("/update")
    public String submitUpdate(@Valid FaqVO faqVO,
                               BindingResult result,
                               HttpServletRequest request,
                               Model model) throws IllegalStateException, IOException {

        log.debug("<<글 수정>> : {}", faqVO);

        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            return "views/faq/faqModify";
        }

        faqService.updateFaq(faqVO);

        model.addAttribute("message", "글 수정 완료!");
        // 수정 후 상세 페이지로 이동 경로 수정
        model.addAttribute("url", request.getContextPath() + "/admin/faq/detail?faq_num=" + faqVO.getFaq_num());

        return "views/common/resultAlert";
    }

    // 공지사항 삭제
    @GetMapping("/delete")
    public String submitDelete(@RequestParam("faq_num") long faq_num) {

        log.debug("<<게시글 삭제>> faq_num : {}", faq_num);

        faqService.deleteFaq(faq_num);

        // 삭제 후 목록으로 이동 경로 수정
        return "redirect:/admin/faq/list";
    }
}
