package kr.spring.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import kr.spring.property.service.PropertyService;
import kr.spring.property.vo.PropertyVO;
import kr.spring.user.service.UserService;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.user.vo.UserVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_REALTOR')")
@RequestMapping("/realtor")
public class UserRealtorController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private PropertyService propertyService;
	

	
	
	// 매물 등록 신청 폼 호출
	@GetMapping("signProperty")
	public String form() {
		return "views/realtor/signProperty"; 
	}
	@PostMapping("signProperty")
	public String submit(@Valid PropertyVO requestVO,
	                     BindingResult result,
	                     @AuthenticationPrincipal PrincipalDetails principal,
	                     Model model) {

	    log.debug("매물 신청 데이터: {}", requestVO);

	    if (result.hasErrors()) {
	        return "views/realtor/signProperty";
	    }

	    UserVO user = principal.getUserVO();
	    requestVO.setRealtor_num(user.getUser_num());

	    propertyService.insertProperty(requestVO);

	    return "redirect:/realtor/signProperty";

	}

	// 관리자가 관리하는 리스트 출력
	@GetMapping("realtorPropertyList")
	public String listForm(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
	                       Model model,
	                       @AuthenticationPrincipal PrincipalDetails principal) {

	    long realtorNum = principal.getUserVO().getUser_num();

	    // 페이징 설정
	    int rowCount = 10; // 한 페이지에 보일 게시물 수
	    int pageCount = 5; // 하단에 보일 페이지 수

	    // 전체 매물 수
	    int totalCount = propertyService.getPropertyCountRealtor(realtorNum);

	    // PagingUtil 사용
	    PagingUtil page = new PagingUtil(currentPage, totalCount, rowCount, pageCount, "realtorPropertyList");

	    // 현재 페이지에 해당하는 매물 목록
	    List<PropertyVO> list = propertyService.getPropertyRealtorPage(realtorNum, page.getStartRow(), page.getEndRow());

	    model.addAttribute("propertyList", list);
	    model.addAttribute("page", page.getPage());

	    return "views/realtor/realtorPropertyList";
	}


	
	@PostMapping("realtorPropertyList")
	public String filterList(@RequestParam Map<String, String> filters,
	                         Model model,
	                         @AuthenticationPrincipal PrincipalDetails principal) {

	    long realtorNum = principal.getUserVO().getUser_num();
	    
	    filters.put("realtor_num", String.valueOf(realtorNum));

	    List<PropertyVO> filteredList = propertyService.getFilterProperty(filters);

	    model.addAttribute("propertyList", filteredList);
	    return "views/realtor/realtorPropertyList";
	}
	
	@GetMapping("/propertyDetail/{id}")
	public String propertyDetail(@PathVariable("id") Long propertyId, Model model) {
	    PropertyVO property = propertyService.getPropertyDetail(propertyId);
		model.addAttribute("property", property);
	return "views/realtor/propertyDetail";
	}
}
