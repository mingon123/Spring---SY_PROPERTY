package kr.spring.notice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import kr.spring.notice.service.NoticeService;
import kr.spring.notice.vo.NoticeVO;
import kr.spring.util.PagingUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/notice")
public class NoticeUserController {

    @Autowired
    private NoticeService noticeService;

    // 자바빈(VO) 초기화
    @ModelAttribute
    public NoticeVO initCommand() {
        return new NoticeVO();
    }

    // 공지사항 목록 (뷰 리턴)
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

        return "views/notice/noticeListUser";
    }

    // 공지사항 상세 JSON 반환 (REST API)
    @GetMapping("/detail/{notice_num}")
    @ResponseBody
    public Map<String, Object> getNoticeDetail(@PathVariable("notice_num") Long notice_num) {
        Map<String, Object> map = new HashMap<>();
        NoticeVO notice = noticeService.selectNotice(notice_num);
        map.put("notice", notice);
        return map;
    }

    // 공지사항 목록 JSON 반환 (REST API)
    @GetMapping("/list-json")
    @ResponseBody
    public Map<String, Object> getListJson(@RequestParam(name="pageNum",defaultValue = "1") int pageNum) {
        int count = noticeService.selectRowCount(new HashMap<>());

        // 페이징 계산
        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("start", page.getStartRow()); 
        paramMap.put("end", page.getEndRow());    

        List<NoticeVO> list = noticeService.selectList(paramMap);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);

        return result;
    }

}
