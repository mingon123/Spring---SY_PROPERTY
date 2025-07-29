package kr.spring.report.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import kr.spring.report.service.ReportService;
import kr.spring.report.vo.ReportVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/report")
public class ReportAdminController {

    @Autowired
    private ReportService reportService;
    
    // 자바빈(VO) 초기화
    @ModelAttribute
    public ReportVO initCommand() {
        return new ReportVO();
    }

    @GetMapping("/list")
    public String getList(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                          Model model) {
        int count = reportService.selectRowCountAll();

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        List<ReportVO> list = null;
        if (count > 0) {
            Map<String,Object> map = new HashMap<>();
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());

            list = reportService.selectListAll(map);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/report/reportList";
    }

    @GetMapping("/detail")
    public String getDetail(@RequestParam("report_num") long report_num,
                            Model model) {
    	
    	log.debug("<<신고 상세>> report_num : {}", report_num);

        ReportVO report = reportService.selectReport(report_num);

        if(report == null) {
          
            return "redirect:/admin/report/list"; 
        }

        model.addAttribute("report", report);

        return "views/report/reportView";
    }


	// 신고 삭제
     @GetMapping("/delete")
     public String submitDelete(@RequestParam("report_num") long report_num,
                                 HttpServletRequest request) {

         log.debug("<<신고 삭제>> report_num : {}", report_num);

         reportService.deleteReport(report_num);

         return "redirect:/admin/report/list";
     }

    @GetMapping("/upload/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename, HttpServletRequest request) {
        try {
            String uploadPath = request.getSession().getServletContext().getRealPath("assets/upload");
            Path file = Paths.get(uploadPath).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);

        } catch (IOException e) {
            log.error("파일 반환 중 에러", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}