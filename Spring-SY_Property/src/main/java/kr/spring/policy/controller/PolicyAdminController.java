package kr.spring.policy.controller;

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

import kr.spring.policy.service.PolicyService;
import kr.spring.policy.vo.PolicyVO;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/policy")
public class PolicyAdminController {

    @Autowired
    private PolicyService policyService;

    // 자바빈(VO) 초기화
    @ModelAttribute
    public PolicyVO initCommand() {
        return new PolicyVO();
    }

    // 글쓰기 폼
    @GetMapping("/write")
    public String form() {
        return "views/policy/policyWrite";
    }

    // 글쓰기 처리
    @PostMapping("/write")
    public String submit(@Valid PolicyVO policyVO,
                         BindingResult result,
                         HttpServletRequest request,
                         Model model) throws IllegalStateException, IOException {

        log.debug("<<운영정책 글등록>> : {}", policyVO);

        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            return form();
        }

        policyService.insertPolicy(policyVO);

        model.addAttribute("message", "글을 정상적으로 등록했습니다.");
        model.addAttribute("url", request.getContextPath() + "/admin/policy/list");

        return "views/common/resultAlert";
    }

    // 운영정책 목록
    @GetMapping("/list")
    public String getList(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                          Model model) {
        int count = policyService.selectRowCount(new HashMap<>());

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        List<PolicyVO> list = null;
        if (count > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());
            list = policyService.selectList(map);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/policy/policyList";
    }

    // 운영정책 상세
    @GetMapping("/detail")
    public String getDetail(@RequestParam("policy_num") long policy_num,
                            Model model) {

        log.debug("<<운영정책 상세>> policy_num : {}", policy_num);

        PolicyVO policy = policyService.selectPolicy(policy_num);
        policy.setTitle(StringUtil.useNoHtml(policy.getTitle()));

        model.addAttribute("policy", policy);

        return "views/policy/policyView";
    }

    // 운영정책 수정 폼
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/update")
    public String formUpdate(@RequestParam("policy_num") long policy_num,
                             Model model) {

    	PolicyVO policyVO = policyService.selectPolicy(policy_num);
        model.addAttribute("policyVO", policyVO);

        return "views/policy/policyModify";
    }

    // 운영정책 수정 처리
   //@PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String submitUpdate(@Valid PolicyVO policyVO,
                                BindingResult result,
                                HttpServletRequest request,
                                Model model) throws IllegalStateException, IOException {

        log.debug("<<글 수정>> : {}", policyVO);

        // 유효성 검사
        if (result.hasErrors()) {
            ValidationUtil.printErrorFields(result);
            return "views/policy/policyModify";
        }

        policyService.updatePolicy(policyVO);

        model.addAttribute("message", "글 수정 완료!");
        model.addAttribute("url", request.getContextPath() + "/admin/policy/detail?policy_num=" + policyVO.getPolicy_num());

        return "views/common/resultAlert";
    }

    // 운영정책 삭제
   //@PreAuthorize("isAuthenticated()")
    @GetMapping("/delete")
    public String submitDelete(@RequestParam("policy_num") long policy_num,
                                HttpServletRequest request) {

        log.debug("<<운영정책 삭제>> policy_num : {}", policy_num);

        policyService.deletePolicy(policy_num);

        return "redirect:/admin/policy/list";
    }
}
