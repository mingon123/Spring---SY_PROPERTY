package kr.spring.map.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.user.service.UserService;
import kr.spring.user.vo.UserVO;

@Controller
@RequestMapping("/map") // 중간경로
public class MapController {

	 @Autowired
	    private UserService userService;

	    @GetMapping
	    public String mapMain(Principal principal, Model model) {
	        if (principal != null) {
	            String username = principal.getName();
	            UserVO user = userService.selectCheckUser(username);
	            model.addAttribute("user_num", user.getUser_num()); //  이걸 넣어줘야 user_num 사용 가능
	            model.addAttribute("loginUser", user); 
	        }
	        return "views/map/mapMain";
	    }
}
