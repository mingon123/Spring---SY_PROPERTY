package kr.spring.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.spring.user.service.UserService;
import kr.spring.user.vo.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserAdminRestController {

    @Autowired
	private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

	// 회원 삭제
    @DeleteMapping("/admin_delete/{user_num}")
	public ResponseEntity<Map<String, String>> deleteUser(
			@PathVariable long user_num, 
			@AuthenticationPrincipal PrincipalDetails principal){
		log.debug("<<회원 삭제>> re_num : {}", user_num);
		
		Map<String, String> mapAjax = new HashMap<String, String>();
		if(principal==null) {
			mapAjax.put("result", "logout");
		}else if(principal.getUserVO().getAuthority().equalsIgnoreCase("ROLE_ADMIN")) {
			// 관리자로 로그인한 경우
			userService.deleteUser(user_num);
			mapAjax.put("result", "success");
		}else {
			mapAjax.put("result", "wrongAccess");
		}
		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);		
	}

	
}
