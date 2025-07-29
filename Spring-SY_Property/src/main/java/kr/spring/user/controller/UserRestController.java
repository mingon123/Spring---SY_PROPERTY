package kr.spring.user.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.spring.user.email.Email;
import kr.spring.user.email.EmailSender;
import kr.spring.user.service.UserService;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.user.vo.RealtorDetailVO;
import kr.spring.user.vo.UserDetailVO;
import kr.spring.user.vo.UserRole;
import kr.spring.user.vo.UserVO;
import kr.spring.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserRestController {

    @Autowired
	private UserService userService;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private Email email;
    @Autowired
    private PasswordEncoder passwordEncoder;

	// 아이디 중복 체크
    @GetMapping("/confirmId/{id}")
    public ResponseEntity<Map<String,String>> checkId(@PathVariable String id){
        log.debug("<<아이디 중복 체크>> : " + id);
		
		Map<String, String> mapAjax = new HashMap<String, String>();
		
		UserVO user = userService.selectCheckId(id);
		if(id!=null) { // 아이디 체크
			if(user!=null) {
				// 아이디 중복
				mapAjax.put("result", "idDuplicated");
			}else {
				if(!Pattern.matches("^[A-Za-z0-9]{4,14}$", id)) {
					// 패턴 불일치
					mapAjax.put("result", "notMatchPattern");
				}else {
					// 패턴 일치하면서 아이디 미중복
					mapAjax.put("result", "idNotFound");
				}
			}
		}else {
			mapAjax.put("result", "error"); // 아이디 전달 안됨
		}
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
	// 별명 중복 체크
	@GetMapping("/confirmNickName/{nick_name}")
	public ResponseEntity<Map<String, String>> checkNickName(@PathVariable String nick_name){
		log.debug("<<별명 중복체크>> : " + nick_name);
		
		Map<String, String> mapAjax = new HashMap<String, String>();
		
		UserVO user = userService.selectCheckNickName(nick_name);
		if(nick_name!=null) { // 별명 체크
			if(user!=null) {
				// 별명 중복
				mapAjax.put("result", "nickDuplicated");
			}else {
				if(!Pattern.matches("^[ㄱ-ㅎ가-힣A-Za-z0-9]{2,10}$", nick_name)) {
					// 패턴 불일치
					mapAjax.put("result", "notMatchPattern");
				}else {
					// 패턴이 일치하면서 별명 미중복
					mapAjax.put("result", "nickNotFound");
				}
			}
		}else {
			mapAjax.put("result", "error");
		}		
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
	// 프로필 사진 업로드
	@PutMapping("/updateMyPhoto")
	public ResponseEntity<Map<String,String>> updateMyPhoto(UserDetailVO userDetailVO, @AuthenticationPrincipal PrincipalDetails principal){
		log.debug("<<프로필 사진 업로드>> : {}", userDetailVO);
		
		Map<String,String> mapAjax = new HashMap<String, String>();
		if(principal==null) {
			mapAjax.put("result", "logout");
		}else {
			userDetailVO.setUser_num(principal.getUserVO().getUser_num());
			userService.updateUserProfile(userDetailVO);
			mapAjax.put("result", "success");
		}
		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);
	}
	
	// 중개사 프로필 사진 업로드
	@PutMapping("/updateMyPhotoRealtor")
	public ResponseEntity<Map<String, String>> updateMyPhotoRealtor(
		    @RequestParam("upload") MultipartFile upload,
		    @AuthenticationPrincipal PrincipalDetails principal) {
	    Map<String, String> mapAjax = new HashMap<>();

	    if (principal == null) {
	        mapAjax.put("result", "logout");
	    } else {
	        RealtorDetailVO realtor = new RealtorDetailVO();
	        realtor.setRealtor_num(principal.getUserVO().getUser_num());
	        try {
	            realtor.setPhoto(upload.getBytes());
	        } catch (IOException e) {
	            e.printStackTrace();
	            mapAjax.put("result", "error");
	            return new ResponseEntity<>(mapAjax, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        realtor.setPhoto_name(upload.getOriginalFilename());

	        userService.updateRealtorProfile(realtor);
	        mapAjax.put("result", "success");
	    }
	    return new ResponseEntity<>(mapAjax, HttpStatus.OK);
	}

	// 비밀번호 찾기  
	@PutMapping("/getPasswordInfo")
	public ResponseEntity<Map<String,String>> sendEmailAction(@RequestBody UserVO userVO){
		log.debug("<<비밀번호 찾기>> : {}", userVO);
		
		Map<String,String> mapAjax = new HashMap<String, String>();
		
		UserVO user = userService.selectIdEmailCheckUser(userVO.getId());

        if (user != null) {
            int auth = user.getAuthorityOrdinal();
            
            // 정지회원
            if (auth == UserRole.SUSPENDED.ordinal()) {
                mapAjax.put("result", "suspended");
                return ResponseEntity.ok(mapAjax);
            }
            
            // 이메일 비교용
            String inputEmail = null;
            String storedEmail = null;
            
            if (auth == UserRole.USER.ordinal()) {
                if (user.getUserDetail() == null || userVO.getUserDetail() == null) {
                    mapAjax.put("result", "invalidInfo");
                    return ResponseEntity.ok(mapAjax);
                }
                inputEmail = userVO.getUserDetail().getEmail();
                storedEmail = user.getUserDetail().getEmail();
            } else if (auth == UserRole.N_REALTOR.ordinal() || auth == UserRole.REALTOR.ordinal()) {
                if (user.getRealtorDetail() == null || userVO.getRealtorDetail() == null) {
                    mapAjax.put("result", "invalidInfo");
                    return ResponseEntity.ok(mapAjax);
                }
                inputEmail = userVO.getRealtorDetail().getEmail();
                storedEmail = user.getRealtorDetail().getEmail();
            } else {
                mapAjax.put("result", "invalidInfo");
                return ResponseEntity.ok(mapAjax);
            }

            // 이메일 일치 여부 확인
            if (storedEmail != null && storedEmail.equals(inputEmail)) {
                String origin_passwd = user.getPasswd();
                String passwd = StringUtil.randomPassword(10);
                user.setPasswd(passwordEncoder.encode(passwd));
                userService.updateRandomPassword(user);

                email.setContent("임시 비밀번호는 " + passwd + " 입니다.");
                email.setReceiver(storedEmail);
                email.setSubject(user.getId() + " 님 비밀번호 찾기 메일입니다.");

                try {
                    emailSender.sendEmail(email);
                    log.debug("<<임시 비밀번호>> : {}", passwd);
                    mapAjax.put("result", "success");
                } catch (Exception e) {
                    log.error("<<이메일 전송 오류>> : {}", e.toString());
                    // 복구
                    user.setPasswd(origin_passwd);
                    userService.updateRandomPassword(user);
                    mapAjax.put("result", "failure");
                }
            } else {
                mapAjax.put("result", "invalidInfo");
            }
        } else {
            mapAjax.put("result", "invalidInfo");
        }
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}

}
