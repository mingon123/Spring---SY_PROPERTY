package kr.spring.main.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.spring.user.security.CustomAccessDeniedHandler;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.user.vo.UserRole;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {
	@GetMapping("/")
	public String init(@AuthenticationPrincipal PrincipalDetails principal) {
		if (principal != null) {
			String role = principal.getUserVO().getAuthority();

			if (role.equals(UserRole.ADMIN.getValue())) {
				return "redirect:/main/admin";
			} else if (role.equals(UserRole.N_REALTOR.getValue())) {
				return "redirect:/main/nRealtor";
			}
		}
		
		return "redirect:/main/main";
	}
	
	@GetMapping("/main/main")
	public String main(Model model) {
		return "views/main/main";
	}
	
	// 관리자 페이지
	@GetMapping("/main/admin")
	public String adminMain(Model model) {
		return "views/main/admin";
	}
	
	// 미인증중개사 페이지
	@GetMapping("/main/nRealtor")
	public String realtorMain(Model model) {
	    return "views/main/nRealtor";
	}
	
	@GetMapping("/accessDenied")
	public String access() {
		return "views/common/accessDenied";
	}
	
	@GetMapping("/fileSizeLimit")
	public String fileSizeLimit() {
		return "views/common/fileSizeLimit";
	}
}
