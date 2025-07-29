package kr.spring.news.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import kr.spring.news.service.NewsService;
import kr.spring.news.vo.NewsVO;

import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/news")
public class NewsUserController {
    @Autowired
    private NewsService newsService;

    // 자바빈(VO) 초기화
    @ModelAttribute
    public NewsVO initCommand() {
        return new NewsVO();
    }

    // news 목록
    @GetMapping("/list")
    public String getList(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                          Model model) {
        int count = newsService.selectRowCount(new HashMap<>());

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        List<NewsVO> list = null;
        if (count > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());
            list = newsService.selectList(map);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/news/newsListUser";
    }

    // news 상세
    @GetMapping("/detail")
    public String getDetail(@RequestParam("news_num") long news_num,
                            Model model) {

        log.debug("<<news 상세>> news_num : {}", news_num);

        NewsVO news = newsService.selectNews(news_num);
        news.setTitle(StringUtil.useNoHtml(news.getTitle()));

        model.addAttribute("news", news);

        return "views/news/newsViewUser";
    }

}
