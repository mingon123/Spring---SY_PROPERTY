package kr.spring.report.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.report.service.ReportService;
import kr.spring.report.vo.ReportVO;
import kr.spring.report.vo.Report_typeVO;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/report")
public class ReportUserController {
    @Autowired
    private ReportService reportService;
    
    @ModelAttribute
    public ReportVO initCommand() {
        return new ReportVO();
    }

    // 신고 작성 폼
    @GetMapping("/write")
    public String form(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        if (principal == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("url", "/user/loginRegisterUser");
            return "views/common/resultAlert";
        }

        Map<String, Object> map = new HashMap<>();
        List<Report_typeVO> typeList = reportService.selectTypeList(map);
        model.addAttribute("typeList", typeList);

        model.addAttribute("reportVO", new ReportVO());

        return "views/report/reportWrite";
    }

    // 신고 등록 처리
    @PostMapping("/write")
    public String submit(@Valid ReportVO reportVO,
            BindingResult result,
            @AuthenticationPrincipal PrincipalDetails principal,
            HttpServletRequest request,  
            Model model) throws IOException {
    	
        if (principal == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("url", "/user/loginRegisterUser");
            return "views/common/resultAlert";
        }
        
        if (reportVO.getUpload() == null || reportVO.getUpload().isEmpty()) {
            model.addAttribute("message", "파일을 첨부해주세요.");
            model.addAttribute("url", "/report/write");
            return "views/common/resultAlert";
        }
        
        Long userNum = principal.getUserVO().getUser_num();

        // 신고 대상자 아이디로부터 suspect_num 조회 후 세팅
        String suspectUserId = reportVO.getSuspect_user_id();
        if (suspectUserId != null && !suspectUserId.trim().isEmpty()) {
            Long suspectNum = reportService.selectUserNumById(suspectUserId.trim());
            if (suspectNum == null) {
                model.addAttribute("message", "존재하지 않는 신고 대상자 아이디입니다.");
                model.addAttribute("url", "/report/write");
                return "views/common/resultAlert";
            }
            reportVO.setSuspect_num(suspectNum);
        } else {
            model.addAttribute("message", "신고 대상자 아이디를 입력하세요.");
            model.addAttribute("url", "/report/write");
            return "views/common/resultAlert";
        }
      
        reportVO.setR_photo(reportVO.getUpload().getBytes());
        reportVO.setR_photo_name(FileUtil.createFile(request, reportVO.getUpload())); 
        reportVO.setReporter_num(userNum);

        reportService.insertReport(reportVO);
        
        model.addAttribute("message", "글을 정상적으로 등록했습니다.");
        model.addAttribute("url", "/report/list");

        return "redirect:/report/list";
    }

    // 신고 목록 페이지 뷰
    @GetMapping("/list")
    public String list(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
                       @AuthenticationPrincipal PrincipalDetails principal,
                       Model model) {
        if (principal == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("url", "/user/loginRegisterUser");
            return "views/common/resultAlert";
        }

        Long userNum = principal.getUserVO().getUser_num();

        Map<String, Object> map = new HashMap<>();
        map.put("reporter_num", userNum);

        int rowCount = reportService.selectRowCount(map);
        PagingUtil page = new PagingUtil(currentPage, rowCount, 10, 10, "/report/list");

        map.put("start", page.getStartRow());
        map.put("end", page.getEndRow());

        List<ReportVO> list = reportService.selectList(map);

        model.addAttribute("count", rowCount);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/report/reportListUser";
    }

    // 신고 목록 JSON 데이터 (AJAX 용)
    @GetMapping("/list-json")
    @ResponseBody
    public Map<String, Object> listJson(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
                                        @AuthenticationPrincipal PrincipalDetails principal) {
        Map<String, Object> result = new HashMap<>();

        if (principal == null) {
            result.put("error", "로그인이 필요합니다.");
            result.put("list", Collections.emptyList());
            result.put("count", 0);
            return result;
        }

        Long userNum = principal.getUserVO().getUser_num();
        Map<String, Object> map = new HashMap<>();
        map.put("reporter_num", userNum);

        int rowCount = reportService.selectRowCount(map);
        PagingUtil page = new PagingUtil(currentPage, rowCount, 10, 10, "/report/list-json");

        map.put("start", page.getStartRow());
        map.put("end", page.getEndRow());

        List<ReportVO> list = reportService.selectList(map);

        result.put("count", rowCount);
        result.put("list", list);
        result.put("page", page.getPage());

        return result;
    }

    // 신고 분류 전체 조회
    @GetMapping("/types")
    @ResponseBody
    public List<Report_typeVO> getReportTypes() {
        Map<String, Object> map = new HashMap<>();
        return reportService.selectTypeList(map);
    }

    // 첨부파일 반환
    @GetMapping("/upload/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        try {
            // 업로드 폴더 절대경로 얻기
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
    
	// 내용 보이기
    @GetMapping("/detail")
    public String getDetail(@RequestParam("report_num") long report_num,
                            @AuthenticationPrincipal PrincipalDetails principal,
                            Model model) {
        if (principal == null) {
            return "redirect:/user/loginRegisterUser";
        }

        Long userNum = principal.getUserVO().getUser_num();

        ReportVO report = reportService.selectReport(report_num);
        if (report == null || !userNum.equals(report.getReporter_num())) {
            model.addAttribute("message", "해당 글에 접근할 권한이 없습니다.");
            model.addAttribute("url", "/report/list");
            return "views/common/resultAlert";
        }

        report.setTitle(StringUtil.useNoHtml(report.getTitle()));
        model.addAttribute("report", report);

        return "views/report/reportListUser";
    }
    
    @GetMapping("/detail/{report_num}")
    @ResponseBody
    public Map<String, Object> getReportDetail(@PathVariable("report_num") Long reportNum,
                                               @AuthenticationPrincipal PrincipalDetails principal) {
        Map<String, Object> result = new HashMap<>();

        if (principal == null) {
            result.put("error", "로그인이 필요합니다.");
            return result;
        }

        Long userNum = principal.getUserVO().getUser_num();
        ReportVO report = reportService.selectReport(reportNum);

        if (report == null) {
            result.put("error", "존재하지 않는 신고입니다.");
        } else if (!userNum.equals(report.getReporter_num())) {
            result.put("error", "해당 신고에 접근할 권한이 없습니다.");
        } else {
            result.put("report", report);
        }

        return result;
    }
    
    //삭제
    @GetMapping("/delete")
    public String submitDelete(@RequestParam("report_num") long report_num,
                               @AuthenticationPrincipal PrincipalDetails principal,
                               HttpServletRequest request,
                               Model model) {
        if (principal == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("url", "/user/loginRegisterUser");
            return "views/common/resultAlert";
        }

        Long userNum = principal.getUserVO().getUser_num();
        ReportVO db_report = reportService.selectReport(report_num);

        if (db_report == null || !userNum.equals(db_report.getReporter_num())) {
            model.addAttribute("message", "해당 글에 접근할 권한이 없습니다.");
            model.addAttribute("url", "/report/list");
            return "views/common/resultAlert";
        }

        reportService.deleteReport(report_num);
        if (db_report.getR_photo_name() != null) {
            FileUtil.removeFile(request, db_report.getR_photo_name()); 
        }

        return "redirect:/report/list";
    }
    
 // 신고 수정 폼 보여주기
    @GetMapping("/update")
    public String formUpdate(@RequestParam("report_num") long report_num,
                             @AuthenticationPrincipal PrincipalDetails principal,
                             Model model) {
        if (principal == null) {
            return "redirect:/user/loginRegisterUser";
        }
        
        Long userNum = principal.getUserVO().getUser_num();
        // 기존 신고 정보 조회
        ReportVO report = reportService.selectReport(report_num);

        if (report == null) {
            model.addAttribute("message", "존재하지 않는 신고입니다.");
            model.addAttribute("url", "/report/list");
            return "views/common/resultAlert";
        }

        // 신고 분류 목록 가져오기
        Map<String, Object> map = new HashMap<>();
        List<Report_typeVO> typeList = reportService.selectTypeList(map);

        // 모델에 데이터 등록
        model.addAttribute("reportVO", report);
        model.addAttribute("typeList", typeList);

        // 수정 폼으로 이동
        return "views/report/reportModify";  
    }
    
    @PostMapping("/update")
    public String submitUpdate(@Valid ReportVO reportVO,
                               BindingResult result,
                               @AuthenticationPrincipal PrincipalDetails principal,
                               HttpServletRequest request,
                               Model model) throws IOException {
        if (principal == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("url", "/user/loginRegisterUser");
            return "views/common/resultAlert";
        }
        

        Long userNum = principal.getUserVO().getUser_num();
        ReportVO db_report = reportService.selectReport(reportVO.getReport_num());

        if (db_report == null || !userNum.equals(db_report.getReporter_num())) {
            model.addAttribute("message", "해당 글에 접근할 권한이 없습니다.");
            model.addAttribute("url", "/report/list");
            return "views/common/resultAlert";
        }
        
        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            reportVO.setR_photo_name(db_report.getR_photo_name());
     
            return "views/report/reportModify";
        }

        // 신고 대상자 아이디로부터 suspect_num 조회 후 세팅
        String suspectUserId = reportVO.getSuspect_user_id();
        if (suspectUserId != null && !suspectUserId.trim().isEmpty()) {
            Long suspectNum = reportService.selectUserNumById(suspectUserId.trim());
            if (suspectNum == null) {
                model.addAttribute("message", "존재하지 않는 신고 대상자 아이디입니다.");
                model.addAttribute("url", "/report/update?report_num=" + reportVO.getReport_num());
                return "views/common/resultAlert";
            }
            reportVO.setSuspect_num(suspectNum);
        } else {
            model.addAttribute("message", "신고 대상자 아이디를 입력하세요.");
            model.addAttribute("url", "/report/update?report_num=" + reportVO.getReport_num());
            return "views/common/resultAlert";
        }

        // 사진 처리
        if ((reportVO.getUpload() == null || reportVO.getUpload().isEmpty()) &&
                (db_report.getR_photo_name() == null || db_report.getR_photo_name().isEmpty())) {
                model.addAttribute("message", "파일을 첨부해주세요.");
                model.addAttribute("url", "/report/update?report_num=" + reportVO.getReport_num());
                return "views/common/resultAlert";
            }

            if (reportVO.getUpload() != null && !reportVO.getUpload().isEmpty()) {
            	reportVO.setR_photo_name(FileUtil.createFile(request, reportVO.getUpload()));  
                FileUtil.removeFile(request, db_report.getR_photo_name());                
            } else {
            	reportVO.setR_photo_name(db_report.getR_photo_name());
            }

            reportVO.setReporter_num(userNum);
            reportService.updateReport(reportVO);

            model.addAttribute("message", "글 수정 완료!");
            model.addAttribute("url", "/report/list");

            return "views/common/resultAlert";
        }
}
