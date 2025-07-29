package kr.spring.qna.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.spring.qna.service.QnaService;
import kr.spring.qna.vo.QnaVO;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/qna")
public class QnaUserController {
    @Autowired
    private QnaService qnaService;

    //문의 유형
    private static final String[] categories = {"서비스 이용문의", "허위매물 신고", "단지정보 문의", "기타문의"};

    @ModelAttribute
    public QnaVO initCommand() {
        return new QnaVO();
    }

    //1:1 문의 작성 폼
    @GetMapping("/write")
    public String form(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        if (principal == null) {
            return "redirect:/user/loginRegisterUser";
        }

        model.addAttribute("categories", categories);
        return "views/qna/qnaWrite";
    }
    
    //1:1 문의 작성 처리
    @PostMapping("/write")
    public String submit(@Valid QnaVO qnaVO,
                         BindingResult result,
                         @AuthenticationPrincipal PrincipalDetails principal,
                         HttpServletRequest request,  
                         Model model) throws IOException {

        if (principal == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("url", "/user/loginRegisterUser");
            return "views/common/resultAlert";
        }

        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            model.addAttribute("categories", categories);
            return "views/qna/qnaWrite";
        }

        if (qnaVO.getUpload() == null || qnaVO.getUpload().isEmpty()) {
            model.addAttribute("message", "파일을 첨부해주세요.");
            model.addAttribute("url", "/qna/write");
            return "views/common/resultAlert";
        }

        Long userNum = principal.getUserVO().getUser_num();

        qnaVO.setPhoto(qnaVO.getUpload().getBytes());
        qnaVO.setPhoto_name(FileUtil.createFile(request, qnaVO.getUpload()));  // null -> request
        qnaVO.setUser_num(userNum);

        qnaService.insertQna(qnaVO);

        model.addAttribute("message", "글을 정상적으로 등록했습니다.");
        model.addAttribute("url", "/qna/list");

        return "views/common/resultAlert";
    }

    //1:1 문의 목록
    @GetMapping("/list")
    public String getList(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                          @AuthenticationPrincipal PrincipalDetails principal,
                          Model model) {
        if (principal == null) {
            return "redirect:/user/loginRegisterUser";
        }

        Long userNum = principal.getUserVO().getUser_num();

        Map<String,Object> param = new HashMap<>();
        param.put("userNum", userNum);

        int count = qnaService.selectRowCount(param);
        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        List<QnaVO> list = null;
        if (count > 0) {
            param.put("start", page.getStartRow());
            param.put("end", page.getEndRow());
            list = qnaService.selectList(param);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/qna/qnaListUser";
    }
    
    // 내용 보이기
    @GetMapping("/detail")
    public String getDetail(@RequestParam("qna_num") long qna_num,
                            @AuthenticationPrincipal PrincipalDetails principal,
                            Model model) {
        if (principal == null) {
            return "redirect:/user/loginRegisterUser";
        }

        Long userNum = principal.getUserVO().getUser_num();

        QnaVO qna = qnaService.selectQna(qna_num);
        if (qna == null || !userNum.equals(qna.getUser_num())) {
            model.addAttribute("message", "해당 글에 접근할 권한이 없습니다.");
            model.addAttribute("url", "/qna/list");
            return "views/common/resultAlert";
        }

        qna.setTitle(StringUtil.useNoHtml(qna.getTitle()));
        model.addAttribute("qna", qna);

        return "views/qna/qnaListUser";
    }
    
    @GetMapping("/detail/{qna_num}")
    @ResponseBody
    public Map<String, Object> getQnaDetailJson(@PathVariable("qna_num") long qna_num,
                                                @AuthenticationPrincipal PrincipalDetails principal) {
        Map<String, Object> map = new HashMap<>();

        if (principal == null) {
            map.put("error", "로그인 필요");
            return map;
        }

        Long userNum = principal.getUserVO().getUser_num();
        QnaVO qna = qnaService.selectQna(qna_num);

        if (qna == null || !userNum.equals(qna.getUser_num())) {
            map.put("error", "접근 불가");
        } else {
            map.put("qna", qna);
        }

        return map;
    }

    //1:1 문의 수정폼
    @GetMapping("/update")
    public String formUpdate(@RequestParam("qna_num") long qna_num,
                             @AuthenticationPrincipal PrincipalDetails principal,
                             Model model) {
        if (principal == null) {
            return "redirect:/user/loginRegisterUser";
        }

        Long userNum = principal.getUserVO().getUser_num();
        QnaVO qnaVO = qnaService.selectQna(qna_num);

        if (qnaVO == null || !userNum.equals(qnaVO.getUser_num())) {
            model.addAttribute("message", "해당 글에 접근할 권한이 없습니다.");
            model.addAttribute("url", "/qna/list");
            return "views/common/resultAlert";
        }

        model.addAttribute("qnaVO", qnaVO);
        model.addAttribute("categories", categories);

        return "views/qna/qnaModify";
    }

    @PostMapping("/update")
    public String submitUpdate(@Valid QnaVO qnaVO,
                               BindingResult result,
                               @AuthenticationPrincipal PrincipalDetails principal,
                               HttpServletRequest request,  // 추가
                               Model model) throws IOException {
        if (principal == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("url", "/user/loginRegisterUser");
            return "views/common/resultAlert";
        }

        Long userNum = principal.getUserVO().getUser_num();
        QnaVO db_qna = qnaService.selectQna(qnaVO.getQna_num());

        if (db_qna == null || !userNum.equals(db_qna.getUser_num())) {
            model.addAttribute("message", "해당 글에 접근할 권한이 없습니다.");
            model.addAttribute("url", "/qna/list");
            return "views/common/resultAlert";
        }

        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            qnaVO.setPhoto_name(db_qna.getPhoto_name());
            model.addAttribute("categories", categories);
            return "views/qna/qnaModify";
        }

        if ((qnaVO.getUpload() == null || qnaVO.getUpload().isEmpty()) &&
            (db_qna.getPhoto_name() == null || db_qna.getPhoto_name().isEmpty())) {
            model.addAttribute("message", "파일을 첨부해주세요.");
            model.addAttribute("url", "/qna/update?qna_num=" + qnaVO.getQna_num());
            return "views/common/resultAlert";
        }

        if (qnaVO.getUpload() != null && !qnaVO.getUpload().isEmpty()) {
            qnaVO.setPhoto_name(FileUtil.createFile(request, qnaVO.getUpload()));  
            FileUtil.removeFile(request, db_qna.getPhoto_name());                
        } else {
            qnaVO.setPhoto_name(db_qna.getPhoto_name());
        }

        qnaVO.setUser_num(userNum);
        qnaService.updateQna(qnaVO);

        model.addAttribute("message", "글 수정 완료!");
        model.addAttribute("url", "/qna/list");

        return "views/common/resultAlert";
    }
    
    //1:1문의 삭제
    @GetMapping("/delete")
    public String submitDelete(@RequestParam("qna_num") long qna_num,
                               @AuthenticationPrincipal PrincipalDetails principal,
                               HttpServletRequest request,   // 추가
                               Model model) {
        if (principal == null) {
            model.addAttribute("message", "로그인이 필요합니다.");
            model.addAttribute("url", "/user/loginRegisterUser");
            return "views/common/resultAlert";
        }

        Long userNum = principal.getUserVO().getUser_num();
        QnaVO db_qna = qnaService.selectQna(qna_num);

        if (db_qna == null || !userNum.equals(db_qna.getUser_num())) {
            model.addAttribute("message", "해당 글에 접근할 권한이 없습니다.");
            model.addAttribute("url", "/qna/list");
            return "views/common/resultAlert";
        }

        qnaService.deleteQna(qna_num);
        if (db_qna.getPhoto_name() != null) {
            FileUtil.removeFile(request, db_qna.getPhoto_name());  // null -> request
        }

        return "redirect:/qna/list";
    }

    @GetMapping("/list-json")
    @ResponseBody
    public Map<String, Object> getListJson(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                           @AuthenticationPrincipal PrincipalDetails principal) {
        Map<String, Object> map = new HashMap<>();

        if (principal == null) {
            map.put("count", 0);
            map.put("list", null);
            return map;
        }

        Long userNum = principal.getUserVO().getUser_num();

        Map<String,Object> param = new HashMap<>();
        param.put("userNum", userNum);

        int count = qnaService.selectRowCount(param);
        map.put("count", count);

        List<QnaVO> list = null;
        if (count > 0) {
            int start = (pageNum - 1) * 10 + 1;
            int end = pageNum * 10;
            param.put("start", start);
            param.put("end", end);
            list = qnaService.selectList(param);
        }

        map.put("list", list);
        return map;
    }
    
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


}