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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import kr.spring.qna.service.QnaService;
import kr.spring.qna.vo.QnaVO;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/qna")
public class QnaAdminController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/list")
    public String getList(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                          Model model) {
        int count = qnaService.selectRowCountAll();

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        List<QnaVO> list = null;
        if (count > 0) {
            Map<String,Object> param = new HashMap<>();
            param.put("start", page.getStartRow());
            param.put("end", page.getEndRow());

            list = qnaService.selectListAll(param);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/qna/qnaListAdmin";
    }

    @GetMapping("/detail")
    public String getDetail(@RequestParam("qna_num") long qna_num,
                            Model model) {
        QnaVO qna = qnaService.selectQna(qna_num);
        if (qna == null) {
            model.addAttribute("message", "해당 글이 존재하지 않습니다.");
            model.addAttribute("url", "/admin/qna/list");
            return "views/common/resultAlert";
        }

        qna.setTitle(StringUtil.useNoHtml(qna.getTitle()));
        model.addAttribute("qna", qna);

        return "views/qna/qnaDetailAdmin";
    }

    @GetMapping("/answer")
    public String formAnswer(@RequestParam("qna_num") long qna_num,
                             Model model) {
        QnaVO qnaVO = qnaService.selectQna(qna_num);
        if (qnaVO == null) {
            model.addAttribute("message", "해당 글이 존재하지 않습니다.");
            model.addAttribute("url", "/admin/qna/list");
            return "views/common/resultAlert";
        }
        model.addAttribute("qnaVO", qnaVO);
        model.addAttribute("answerStatuses", new String[]{"미답변", "답변완료"});
        return "views/qna/qnaAnswer";
    }

    @PostMapping("/answer")
    public String submitAnswer(@Valid QnaVO qnaVO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            model.addAttribute("qnaVO", qnaVO);
            model.addAttribute("answerStatuses", new String[]{"미답변", "답변완료"});
            return "views/qna/qnaAnswer";
        }

        QnaVO db_qna = qnaService.selectQna(qnaVO.getQna_num());
        if (db_qna == null) {
            model.addAttribute("message", "해당 글이 존재하지 않습니다.");
            model.addAttribute("url", "/admin/qna/list");
            return "views/common/resultAlert";
        }

        db_qna.setAnswer(qnaVO.getAnswer());
        db_qna.setAnswer_status(qnaVO.getAnswer_status());

        qnaService.updateAnswer(db_qna);

        model.addAttribute("message", "답변이 저장되었습니다.");
        model.addAttribute("url", "/admin/qna/detail?qna_num=" + db_qna.getQna_num());

        return "views/common/resultAlert";
    }

    @GetMapping("/delete")
    public String submitDelete(@RequestParam("qna_num") long qna_num,
                               HttpServletRequest request,
                               Model model) {
        QnaVO db_qna = qnaService.selectQna(qna_num);

        if (db_qna == null) {
            model.addAttribute("message", "해당 글이 존재하지 않습니다.");
            model.addAttribute("url", "/admin/qna/list");
            return "views/common/resultAlert";
        }

        qnaService.deleteQna(qna_num);
        if (db_qna.getPhoto_name() != null) {
            FileUtil.removeFile(request, db_qna.getPhoto_name());
        }

        return "redirect:/admin/qna/list";
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

    @GetMapping("/list-json")
    @ResponseBody
    public Map<String, Object> getListJson(@RequestParam(name="pageNum", defaultValue="1") int pageNum) {
        int count = qnaService.selectRowCountAll();

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list-json", null);

        List<QnaVO> list = null;
        if(count > 0) {
            Map<String,Object> param = new HashMap<>();
            param.put("start", page.getStartRow());
            param.put("end", page.getEndRow());

            list = qnaService.selectListAll(param);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("count", count);
        result.put("page", page.getPage());

        return result;
    }

    @GetMapping("/detail-json/{qna_num}")
    @ResponseBody
    public Map<String, Object> getDetailJson(@PathVariable("qna_num") long qna_num) {
        QnaVO qna = qnaService.selectQna(qna_num);
        Map<String, Object> result = new HashMap<>();
        result.put("qna", qna);
        return result;
    }

    @PostMapping("/answer-json")
    @ResponseBody
    public Map<String, Object> submitAnswerJson(@RequestBody QnaVO qnaVO) {
        Map<String, Object> result = new HashMap<>();

        QnaVO db_qna = qnaService.selectQna(qnaVO.getQna_num());
        if(db_qna == null) {
            result.put("result", "error");
            result.put("message", "해당 글이 존재하지 않습니다.");
            return result;
        }

        db_qna.setAnswer(qnaVO.getAnswer());
        db_qna.setAnswer_status(qnaVO.getAnswer_status());
        qnaService.updateAnswer(db_qna);

        result.put("result", "success");
        return result;
    }
    
    @PostMapping("/answer-delete-json")
    @ResponseBody
    public Map<String, Object> deleteAnswerJson(@RequestBody Map<String, Object> payload) {
        Map<String, Object> result = new HashMap<>();

        try {
            Long qnaNum = Long.valueOf(payload.get("qna_num").toString());
            QnaVO db_qna = qnaService.selectQna(qnaNum);
            if(db_qna == null) {
                result.put("result", "error");
                result.put("message", "해당 글이 존재하지 않습니다.");
                return result;
            }

            db_qna.setAnswer(null);
            db_qna.setAnswer_status("미답변");
            qnaService.updateAnswer(db_qna);

            result.put("result", "success");
        } catch (Exception e) {
            result.put("result", "error");
            result.put("message", "답변 삭제 중 오류가 발생했습니다.");
        }

        return result;
    }

}
