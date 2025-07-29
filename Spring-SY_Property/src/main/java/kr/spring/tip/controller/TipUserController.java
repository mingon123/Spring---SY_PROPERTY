package kr.spring.tip.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import kr.spring.tip.service.TipService;
import kr.spring.tip.vo.TipVO;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/tip")
public class TipUserController {

    @Autowired
    private TipService tipService;

    // VO 초기화
    @ModelAttribute
    public TipVO initCommand() {
        return new TipVO();
    }

    // 목록
    @GetMapping("/list")
    public String getList(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                          Model model) {
        int count = tipService.selectRowCount(new HashMap<>());

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        List<TipVO> list = null;
        if (count > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());
            list = tipService.selectListUser(map);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/tip/tipListUser";
    }

    // 상세
    @GetMapping("/detail")
    public String getDetail(@RequestParam("tip_num") long tip_num, Model model) {
        log.debug("<<TIP 상세>> tip_num: {}", tip_num);

        TipVO tip = tipService.selectTip(tip_num);
        tip.setTitle(StringUtil.useNoHtml(tip.getTitle()));
        model.addAttribute("tip", tip);

        return "views/tip/tipViewUser";
    }

    // 이미지 출력
    @GetMapping("/photo/{tip_num}")
    @ResponseBody
    public ResponseEntity<byte[]> getPhoto(@PathVariable("tip_num") long tip_num) {
        TipVO tip = tipService.selectTip(tip_num);
        if (tip == null || tip.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();

        // 이미지 형식 추정
        if (tip.getPhoto_name() != null) {
            String lowerName = tip.getPhoto_name().toLowerCase();
            if (lowerName.endsWith(".png")) {
                headers.setContentType(MediaType.IMAGE_PNG);
            } else if (lowerName.endsWith(".gif")) {
                headers.setContentType(MediaType.IMAGE_GIF);
            } else if (lowerName.endsWith(".jpeg") || lowerName.endsWith(".jpg")) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        return new ResponseEntity<>(tip.getPhoto(), headers, HttpStatus.OK);
    }
}
