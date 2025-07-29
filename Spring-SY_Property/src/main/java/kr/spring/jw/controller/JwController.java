package kr.spring.jw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.user.security.CustomAccessDeniedHandler;
import kr.spring.user.service.UserService;
import kr.spring.user.vo.UserVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/jw") // 중간 경로
public class JwController {

	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private UserService memberService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	JwController(CustomAccessDeniedHandler customAccessDeniedHandler) {
		this.customAccessDeniedHandler = customAccessDeniedHandler;
	} // 비밀번호 암호화

	// 자바빈(VO) 초기화
	@ModelAttribute
	public UserVO initCommand() {
		return new UserVO();
	}

	//지도 호출
	@GetMapping("/jwTest")
	public String testForm() {
		return "views/user/jw_Test";
	}

}