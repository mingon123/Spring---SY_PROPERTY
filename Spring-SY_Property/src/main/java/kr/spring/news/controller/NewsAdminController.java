package kr.spring.news.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.news.service.NewsService;
import kr.spring.news.vo.NewsVO;

import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/news")
public class NewsAdminController {
    @Autowired
    private NewsService newsService;

    // 자바빈(VO) 초기화
    @ModelAttribute
    public NewsVO initCommand() {
        return new NewsVO();
    }

    // 글쓰기 폼
    @GetMapping("/write")
    public String form() {
        return "views/news/newsWrite";
    }

    // 글쓰기 처리
    @PostMapping("/write")
    public String submit(@Valid NewsVO newsVO,
                         BindingResult result,
                         HttpServletRequest request,
                         Model model) throws IllegalStateException, IOException {

        log.debug("<<news 글등록>> : {}", newsVO);

        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            return form();
        }
        // 파일 업로드 처리
        newsVO.setPhoto_name(FileUtil.createFile(request, newsVO.getUpload()));
        newsService.insertNews(newsVO);

        model.addAttribute("message", "글을 정상적으로 등록했습니다.");
        model.addAttribute("url", request.getContextPath() + "/admin/news/list");

        return "views/common/resultAlert";
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

        return "views/news/newsList";
    }

    // news 상세
    @GetMapping("/detail")
    public String getDetail(@RequestParam("news_num") long news_num,
                            Model model) {

        log.debug("<<news 상세>> news_num : {}", news_num);

        NewsVO news = newsService.selectNews(news_num);
        news.setTitle(StringUtil.useNoHtml(news.getTitle()));

        model.addAttribute("news", news);

        return "views/news/newsView";
    }

    // news 수정 폼
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/update")
    public String formUpdate(@RequestParam("news_num") long news_num,
                             Model model) {

        NewsVO newsVO = newsService.selectNews(news_num);
        model.addAttribute("newsVO", newsVO);

        return "views/news/newsModify";
    }

    // news 수정 처리
    //@PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String submitUpdate(@Valid NewsVO newsVO,
                               BindingResult result,
                               HttpServletRequest request,
                               Model model) throws IllegalStateException, IOException {

        log.debug("<<글 수정>> : {}", newsVO);
        NewsVO db_news = newsService.selectNews(newsVO.getNews_num());
        // 유효성 검사
        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            newsVO.setPhoto_name(db_news.getPhoto_name());
            return "views/news/newsModify";
        }

        // 파일명 셋팅
        newsVO.setPhoto_name(FileUtil.createFile(request, newsVO.getUpload()));

        newsService.updateNews(newsVO);

        if (newsVO.getUpload() != null && !newsVO.getUpload().isEmpty()) {
            // 수정전 파일 삭제 처리
            FileUtil.removeFile(request, db_news.getPhoto_name());
        }

        model.addAttribute("message", "글 수정 완료!");
        model.addAttribute("url", request.getContextPath() + "/admin/news/detail?news_num=" + newsVO.getNews_num());

        return "views/common/resultAlert";
    }

    // news 삭제
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/delete")
    public String submitDelete(@RequestParam("news_num") long news_num,
                               HttpServletRequest request) {

        log.debug("<<news 삭제>> news_num : {}", news_num);

        NewsVO db_news = newsService.selectNews(news_num);

        newsService.deleteNews(news_num);

        if (db_news.getPhoto_name() != null) {
            // 파일 삭제
            FileUtil.removeFile(request, db_news.getPhoto_name());
        }

        return "redirect:/admin/news/list";
    }
}
