package kr.spring.property.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.property.service.PropertyEditRequestService;
import kr.spring.property.service.PropertyService;
import kr.spring.property.vo.PropertyEditRequestVO;
import kr.spring.user.service.UserService;
import kr.spring.user.vo.UserVO;
import kr.spring.util.PagingUtil;
import lombok.RequiredArgsConstructor;
@Controller
@RequiredArgsConstructor
@RequestMapping("/realtor")
public class PropertyEditRequestController {

    private final PropertyEditRequestService propertyEditRequestService;
    private final UserService userService;
    private final PropertyService propertyService;

    
    /**
     * 수정 요청 등록 처리
     */
    @PostMapping("/requestEdit")
    public String requestEdit(@ModelAttribute PropertyEditRequestVO vo, Principal principal) {
        // Principal에서 로그인된 아이디를 가져오고 → UserVO 조회
        UserVO loginUser = userService.selectCheckUser(principal.getName());
        Long userNum = loginUser.getUser_num();

        vo.setUserNum(userNum);

        propertyEditRequestService.submitEditRequest(vo);

        // 수정 요청 후, 매물 상세 페이지로 리다이렉트
        return "redirect:/realtor/propertyDetail/" + vo.getPropertyId();

    }
    @GetMapping("/editRequestList")
    public String getAllEditRequests(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage, Model model, Principal principal) {
        UserVO loginUser = userService.selectCheckUser(principal.getName());
        Long userNum = loginUser.getUser_num();
        
        
        int count = propertyEditRequestService.getRequestCountRealtor(userNum);
        List<PropertyEditRequestVO> allRequests = propertyEditRequestService.getRequestsRealtor(userNum);

        PagingUtil page = new PagingUtil(currentPage, count, 10, 10, "/realtor/editRequestList");
        
        List<PropertyEditRequestVO> pageRequests = propertyEditRequestService.getRequestsPage(
                userNum, page.getStartRow(), page.getEndRow());
       
        model.addAttribute("editRequests", allRequests);
        allRequests.forEach(vo -> {
            System.out.println("요청일: " + vo.getRequestDate());
            System.out.println("매물번호: " + vo.getRoomNumber());
            System.out.println("주소: " + vo.getAddress());
        });
        return "views/realtor/propertyEditRequestList";
    }


   
}
