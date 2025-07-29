package kr.spring.faq.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import kr.spring.faq.service.FaqService;
import kr.spring.faq.vo.FaqVO;
import kr.spring.user.security.CustomAccessDeniedHandler;
import kr.spring.util.PagingUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/faq")
public class FaqUserController {

    @Autowired
    private FaqService faqService;

    // 자바빈(VO) 초기화
    @ModelAttribute
    public FaqVO initCommand() {
        return new FaqVO();
    }

    // FAQ 목록 (화면)
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

        return "views/faq/faqListUser";
    }

    // FAQ 상세 (Ajax JSON)
    @GetMapping("/detail/{faq_num}")
    @ResponseBody
    public Map<String, Object> getFaqDetail(@PathVariable("faq_num") Long faq_num) {
        Map<String, Object> map = new HashMap<>();
        FaqVO faq = faqService.selectFaq(faq_num);
        map.put("faq", faq);
        return map;
    }

    // FAQ 목록 JSON (Ajax 목록용)
    @GetMapping("/list-json")
    @ResponseBody
    public Map<String, Object> getListJson(@RequestParam(name="pageNum", defaultValue = "1") int pageNum) {
        int count = faqService.selectRowCount(new HashMap<>());

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("start", page.getStartRow());
        paramMap.put("end", page.getEndRow());

        List<FaqVO> list = faqService.selectList(paramMap);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);

        return result;
    }
}
