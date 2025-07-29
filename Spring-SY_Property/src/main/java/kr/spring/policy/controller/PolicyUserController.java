package kr.spring.policy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import kr.spring.policy.service.PolicyService;
import kr.spring.policy.vo.PolicyVO;
import kr.spring.util.PagingUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/policy")
public class PolicyUserController {

    @Autowired
    private PolicyService policyService;

    // 자바빈(VO) 초기화
    @ModelAttribute
    public PolicyVO initCommand() {
        return new PolicyVO();
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

        return "views/policy/policyListUser";
    }

    //운영정책 상세
    
    @RestController
    @RequestMapping("/policy")
    public class PolicyController {

        @GetMapping("/detail/{policy_num}")
        public Map<String, Object> getPolicyDetail(@PathVariable("policy_num") Long policy_num) {
            Map<String, Object> map = new HashMap<>();
            PolicyVO policy = policyService.selectPolicy(policy_num);
            map.put("policy", policy);
            return map;
        }
    }


    //운영정책 json
    @GetMapping("/list-json")
    @ResponseBody
    public Map<String, Object> getListJson(@RequestParam(name="pageNum",defaultValue = "1") int pageNum) {
        int count = policyService.selectRowCount(new HashMap<>());

        // 페이징 계산
        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "list", null);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("start", page.getStartRow()); 
        paramMap.put("end", page.getEndRow());    

        List<PolicyVO> list = policyService.selectList(paramMap);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);

        return result;
    }
}