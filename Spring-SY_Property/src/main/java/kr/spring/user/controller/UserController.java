package kr.spring.user.controller;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.spring.user.security.CustomAccessDeniedHandler;
import kr.spring.user.service.UserService;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.user.vo.RealtorDetailVO;
import kr.spring.user.vo.UserDetailVO;
import kr.spring.user.vo.UserRole;
import kr.spring.user.vo.UserVO;
import kr.spring.util.AuthCheckException;
import kr.spring.util.FileUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

    UserController(CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    } // 비밀번호 암호화
	
	// 자바빈(VO) 초기화
    @ModelAttribute
    public UserVO initCommand(@AuthenticationPrincipal PrincipalDetails principal) {
        UserVO userVO = new UserVO();
        
        if (principal != null && principal.getUserVO() != null) {
            String userType = principal.getUserVO().getUser_type();
            if ("USER".equalsIgnoreCase(userType)) userVO.setUserDetail(new UserDetailVO());
            else if ("REALTOR".equalsIgnoreCase(userType)) userVO.setRealtorDetail(new RealtorDetailVO());
        }
        
        return userVO;
    }
    
	// 회원가입/로그인 메뉴
	@GetMapping("/loginRegisterUser")
	public String formLoginRegister() {
		return "views/user/userLoginRegister";
	}
	
	// 회원가입 약관동의
	@GetMapping("/RegisterTerms")
	public String formLoginTerms () {
		return "views/user/userRegisterTerms";
	}
	
	// 중개사 회원가입 약관동의
	@GetMapping("/RegisterRealtorTerms")
	public String formLoginRegisterTerms () {
		return "views/user/realtorRegisterTerms";
	}
	
	// 회원가입 폼 호출
	@GetMapping("/registerUser")
	public String form() {
		return "views/user/userRegister";
	}
	
	// 회원가입 처리
	@PostMapping("/registerUser")
	public String submit(@Valid UserVO userVO, BindingResult result, Model model, HttpServletRequest request) {
		log.debug("<<회원가입>> : " + userVO);
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasFieldErrors("id") || result.hasFieldErrors("passwd")) {
			// 유효성 체크 결과 오류 필드 출력
			ValidationUtil.printErrorFields(result);
			return "views/user/userRegister";
		}

		// 비밀번호 확인 일치여부 검사
		String confirmPasswd = request.getParameter("confirmPasswd");
		if(!userVO.getPasswd().equals(confirmPasswd)) {
		    result.rejectValue("passwd", null, "비밀번호가 일치하지 않습니다.");
		    return "views/user/userRegister";
		}
	    
		
		// Spring Security 암호화
		userVO.setPasswd(passwordEncoder.encode(userVO.getPasswd()));

		// 회원가입
		userService.insertUser(userVO);
		
		// 결과 메시지 처리
		model.addAttribute("accessTitle", "회원가입 완료");
		model.addAttribute("accessMsg", "회원가입이 정상적으로 완료되었습니다.");
		model.addAttribute("accessBtn", "로그인");
		model.addAttribute("accessUrl", request.getContextPath()+"/user/login");
		model.addAttribute("accessBtn2", "홈으로");
		model.addAttribute("accessUrl2", request.getContextPath()+"/main/main");
		
		return "views/common/resultView2";
	}
	
	// 중개사 회원가입 폼 호출
	@GetMapping("/registerRealtor")
	public String registerForm() {
		return "views/user/realtorRegister";
	}
	
	// 중개사 회원가입 처리
	@PostMapping("/registerRealtor")
	public String registerSubmit(@Valid UserVO userVO, BindingResult result, Model model, HttpServletRequest request) throws IOException {
		log.debug("<<중개사 회원가입>> : " + userVO);
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasFieldErrors("id") || result.hasFieldErrors("passwd")) {
			// 유효성 체크 결과 오류 필드 출력
			ValidationUtil.printErrorFields(result);
			return "views/user/realtorRegister";
		}

		// 비밀번호 확인 일치여부 검사
		String confirmPasswd = request.getParameter("confirmPasswd");
		if(!userVO.getPasswd().equals(confirmPasswd)) {
		    result.rejectValue("passwd", null, "비밀번호가 일치하지 않습니다.");
		    return "views/user/realtorRegister";
		}
		
	    // 비밀번호 암호화
	    userVO.setPasswd(passwordEncoder.encode(userVO.getPasswd()));
	    // 권한 설정: 미인증 중개사
	    userVO.setAuthority(UserRole.N_REALTOR.getValue());
	    // 파일 저장 처리
		userVO.getRealtorDetail().setCerUpload(userVO.getRealtorDetail().getCerUpload());
		
		// 회원가입
		userService.insertRealtor(userVO);

		// 결과 메시지 처리
		model.addAttribute("accessTitle", "회원가입 완료");
		model.addAttribute("accessMsg", "회원가입이 정상적으로 완료되었습니다.");
		model.addAttribute("accessBtn", "로그인");
		model.addAttribute("accessUrl", request.getContextPath()+"/user/login");
		model.addAttribute("accessBtn2", "홈으로");
		model.addAttribute("accessUrl2", request.getContextPath()+"/main/main");
		
		return "views/common/resultView2";
	}
	
	// 로그인 폼 호출
	@GetMapping("/login")
	public String formLogin() {
		return "views/user/userLogin";
	}
	
	// 마이페이지 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage")
	public String getMyPage(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
	    String userType = principal.getUserVO().getUser_type();
	    long userNum = principal.getUserVO().getUser_num();
	    
	    UserVO user;
	    if ("REALTOR".equalsIgnoreCase(userType)) {
	        user = userService.selectRealtor(userNum); // 중개사
	    } else {
	        user = userService.selectUser(userNum); // 일반회원
	    }
	    log.debug("<< 마이페이지 진입 >> user_type = {}", user.getUser_type());
		model.addAttribute("user", user);
		
		return "views/user/userMyPage";
	}
	
	// 회원정보수정 폼 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/updateUser")
	public String formUpdate(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
		// 회원정보
		UserVO userVO = userService.selectUser(principal.getUserVO().getUser_num());
	    if (userVO == null) userVO = new UserVO();
	    if (userVO.getUserDetail() == null) userVO.setUserDetail(new UserDetailVO());
	    if (userVO.getUser_type() == null) {
		    userVO.setUser_type(principal.getUserVO().getUser_type());
		}
		model.addAttribute("userVO", userVO);		
		
		return "views/user/userModify";
	}
	
	// 회원정보수정 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/updateUser")
	public String submitUpdate(@Valid UserVO userVO, BindingResult result, @AuthenticationPrincipal PrincipalDetails principal) {
	    log.debug("<<회원정보수정 요청됨>>");
	    log.debug("입력된 유저 정보: {}", userVO);

	    if ("USER".equalsIgnoreCase(userVO.getUser_type())) {
	        userVO.setRealtorDetail(null);
	    }
	    
	    // 유효성 체크 결과 오류가 있으면 폼 호출
	    if(result.hasErrors()) {
	        log.error("유효성 오류 발생");
	        ValidationUtil.printErrorFields(result);
	        return "views/user/userModify";
	    }

	    userVO.setUser_num(principal.getUserVO().getUser_num());
	    log.debug("수정할 회원번호: {}", userVO.getUser_num());

	    // 회원정보수정
	    userService.updateUser(userVO);
	    log.debug("기본 회원 정보 수정 완료");

	    if (userVO.getUserDetail() != null) {
	        UserVO dbVO = userService.selectUser(userVO.getUser_num());
	        userVO.getUserDetail().setUser_num(userVO.getUser_num());
	        if (dbVO == null || dbVO.getUserDetail() == null) {
	            userService.insertUserDetail(userVO.getUserDetail());
	            log.debug("user_detail 새로 삽입 완료");
	        } else {
	            userService.updateUserDetail(userVO.getUserDetail());
	            log.debug("user_detail 수정 완료");
	        }
	    } else {
	        log.warn("userDetail 정보가 null입니다");
	    }

	    // PrincipalDetails 갱신
	    principal.getUserVO().setNick_name(userVO.getNick_name());
	    if (principal.getUserVO().getUserDetail() != null && userVO.getUserDetail() != null) {
	        principal.getUserVO().getUserDetail().setEmail(userVO.getUserDetail().getEmail());
	    }
	    log.debug("PrincipalDetails 정보 갱신 완료");

	    return "redirect:/user/myPage";
	}

	
	// 미인증 중개사 MY페이지 호출
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_N_REALTOR')")
	@GetMapping("/nRealtorMyPage")
	public String getnRealtorMyPage(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
	    UserVO user = userService.selectRealtor(principal.getUserVO().getUser_num());
	    if (user == null) user = new UserVO();
	    if (user.getRealtorDetail() == null) user.setRealtorDetail(new RealtorDetailVO());
	    model.addAttribute("user", user);
	    return "views/user/nRealtorMyPage";
	}
	
	// 미인증 중개사 정보수정 폼 호출
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_N_REALTOR')")
	@GetMapping("/nRealtorUpdate")
	public String nRealtorFormUpdate(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
		// 회원정보
		UserVO userVO = userService.selectRealtor(principal.getUserVO().getUser_num());
		if (userVO == null) userVO = new UserVO();
	    if (userVO.getRealtorDetail() == null) userVO.setRealtorDetail(new RealtorDetailVO());
	    userVO.getRealtorDetail().setRealtor_num(userVO.getUser_num());
	    
		model.addAttribute("userVO", userVO);		
		return "views/user/nRealtorUpdate";
	}
	
	// 미인증 중개사 회원정보수정 처리
	@PreAuthorize("isAuthenticated() and hasAuthority('ROLE_N_REALTOR')")
	@PostMapping("/nRealtorUpdate")
	public String nRealtorSubmitUpdate(@Valid UserVO userVO, BindingResult result, @AuthenticationPrincipal PrincipalDetails principal) throws IOException {
		log.debug("<<미인증중개사 정보수정 : >> {}", userVO);
		
	    Long userNum = principal.getUserVO().getUser_num();
	    userVO.setUser_num(userNum);

	    if (userVO.getRealtorDetail() == null) userVO.setRealtorDetail(new RealtorDetailVO());
	    RealtorDetailVO detailVO = userVO.getRealtorDetail();
	    detailVO.setRealtor_num(userNum);
	    MultipartFile cerUpload = detailVO.getCerUpload();

	    // 새로 업로드 안 했으면 기존 파일 유지
	    if (cerUpload == null || cerUpload.isEmpty()) {
	        UserVO db_user = userService.selectRealtor(userNum);
	        if (db_user != null && db_user.getRealtorDetail() != null) {
	            RealtorDetailVO dbDetail = db_user.getRealtorDetail();
	            detailVO.setCertificate(dbDetail.getCertificate());
	            detailVO.setCertificate_name(dbDetail.getCertificate_name());
	        }
	    }
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
	    if (
	    	    result.hasFieldErrors("realtorDetail.name") || 
	    	    result.hasFieldErrors("realtorDetail.phone") ||
	    	    result.hasFieldErrors("realtorDetail.email") ||
	    	    result.hasFieldErrors("realtorDetail.zipcode") ||
	    	    result.hasFieldErrors("realtorDetail.address1") ||
	    	    result.hasFieldErrors("realtorDetail.address2") ||
	    	    result.hasFieldErrors("realtorDetail.cerUpload")
	    	) {
	    	    ValidationUtil.printErrorFields(result);
	    	    return "views/user/nRealtorUpdate";
	    	}

		userService.updateUser(userVO);
		userService.saveOrUpdateRealtorDetail(userVO);
		
		// PrincipalDetails에 저장된 자바빈(VO)의 nick_name, email 정보 갱신
		principal.getUserVO().setNick_name(userVO.getNick_name());
		principal.getUserVO().setRealtorDetail(userVO.getRealtorDetail());
		return "redirect:/user/nRealtorMyPage";
	}
	
	// 중개사정보 수정 폼 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/updateRealtor")
	public String realtorFormUpdate(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
		// 회원정보
		UserVO userVO = userService.selectRealtor(principal.getUserVO().getUser_num());
	    if (userVO == null) userVO = new UserVO();
	    if (userVO.getRealtorDetail() == null) userVO.setRealtorDetail(new RealtorDetailVO());
	    if (userVO.getUser_type() == null) {
		    userVO.setUser_type(principal.getUserVO().getUser_type());
		}
	    log.debug("회원정보: {}", userVO);
	    log.debug("중개사 정보: {}", userVO.getRealtorDetail());
	    
		model.addAttribute("userVO", userVO);		
		
		return "views/user/realtorModify";
	}
	
	// 중개사정보 수정 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/updateRealtor")
	public String realtorSubmitUpdate(@Valid UserVO userVO, BindingResult result, @AuthenticationPrincipal PrincipalDetails principal) {
	    log.debug("<<중개사정보수정 요청됨>>");
	    log.debug("입력된 유저 정보: {}", userVO);

	    if ("REALTOR".equalsIgnoreCase(userVO.getUser_type())) {
	        userVO.setUserDetail(null);
	    }
	    
	    // 유효성 체크 결과 오류가 있으면 폼 호출
	    if(result.hasErrors()) {
	        log.error("유효성 오류 발생");
	        ValidationUtil.printErrorFields(result);
	        return "views/user/realtorModify";
	    }

	    userVO.setUser_num(principal.getUserVO().getUser_num());
	    log.debug("수정할 중개사 번호: {}", userVO.getUser_num());

	    // 회원정보수정
	    userService.updateUser(userVO);
	    log.debug("기본 중개사 정보 수정 완료");

	    if (userVO.getRealtorDetail() != null) {
	        UserVO dbVO = userService.selectRealtor(userVO.getUser_num());
	        userVO.getRealtorDetail().setRealtor_num(userVO.getUser_num());
	        if (dbVO == null || dbVO.getRealtorDetail() == null) {
	            userService.insertRealtorDetail(userVO.getRealtorDetail());
	            log.debug("realtor_detail 새로 삽입 완료");
	        } else {
	            userService.updateRealtorDetail(userVO.getRealtorDetail());
	            log.debug("realtor_detail 수정 완료");
	        }
	    } else {
	        log.warn("rDetail 정보가 null입니다");
	    }

	    // PrincipalDetails 갱신
	    principal.getUserVO().setNick_name(userVO.getNick_name());
	    if (principal.getUserVO().getRealtorDetail() != null && userVO.getRealtorDetail() != null) {
	        principal.getUserVO().getRealtorDetail().setEmail(userVO.getRealtorDetail().getEmail());
	    }
	    log.debug("PrincipalDetails 정보 갱신 완료");

	    return "redirect:/user/myPage";
	}
	
	// 프로필 사진 출력(로그인 전용)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/photoView")
	public String getProfile(@AuthenticationPrincipal PrincipalDetails principal, HttpServletRequest request, Model model) {
		try {
			UserVO user = principal.getUserVO();
			log.debug("<<photoView>> : {}", user);
			UserVO userVO = userService.selectUser(user.getUser_num());
			viewProfile(userVO, request, model);			
		} catch (Exception e) {
			getBasicProfileImage(request, model);
		}
		return "imageView";
	}
	
	
	// 프로필 사진 출력(회원번호 지정)
	@GetMapping("/viewProfile")
	public String getProfileByMem_num(long user_num, HttpServletRequest request, Model model) {
		UserVO userVO = userService.selectUser(user_num);
		viewProfile(userVO, request, model);
		
		return "imageView";
	}
	

	// 프로필 사진 처리를 위한 공통 코드
	public void viewProfile(UserVO userVO, HttpServletRequest request, Model model) {
		if(userVO == null) {
			getBasicProfileImage(request, model);
			return;
		}
		
		// 일반회원 프로필
		if(userVO.getUserDetail() != null && userVO.getUserDetail().getPhoto_name() != null) {
			model.addAttribute("imageFile", userVO.getUserDetail().getPhoto());
			model.addAttribute("filename", userVO.getUserDetail().getPhoto_name());
			return;
		}
		
		// 기본 이미지
		getBasicProfileImage(request, model);
	}
	
	// 기본 이미지 읽기
	public void getBasicProfileImage(HttpServletRequest request, Model model) {
		byte[] readbyte = FileUtil.getBytes(request.getServletContext().getRealPath("/assets/image_bundle/face.png"));
		model.addAttribute("imageFile", readbyte);
		model.addAttribute("filename", "face.png");
	}
	
	// 중개사 프로필 사진 출력(로그인 전용)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/realtorPhotoView")
	public String getRealtorProfile(@AuthenticationPrincipal PrincipalDetails principal, HttpServletRequest request, Model model) {
		try {
			UserVO user = principal.getUserVO();
			log.debug("<<photoView>> : {}", user);
			UserVO userVO = userService.selectRealtor(user.getUser_num());
			viewRealtorProfile(userVO, request, model);			
		} catch (Exception e) {
			getBasicProfileImage(request, model);
		}
		return "imageView";
	}
	
	
	// 중개사 프로필 사진 출력(회원번호 지정)
	@GetMapping("/realtorViewProfile")
	public String getRealtorProfileByMem_num(long user_num, HttpServletRequest request, Model model) {
		UserVO userVO = userService.selectRealtor(user_num);
		viewRealtorProfile(userVO, request, model);
		return "imageView";
	}
	

	// 프로필 사진 처리를 위한 공통 코드
	public void viewRealtorProfile(UserVO userVO, HttpServletRequest request, Model model) {
		if(userVO == null) {
			getBasicProfileImage(request, model);
			return;
		}

		// 중개사 프로필
		if(userVO.getRealtorDetail() != null && userVO.getRealtorDetail().getPhoto_name() != null) {
			model.addAttribute("imageFile", userVO.getRealtorDetail().getPhoto());
			model.addAttribute("filename", userVO.getRealtorDetail().getPhoto_name());
			return;
		}
		
		// 기본 이미지
		getBasicProfileImage(request, model);
	}
	
	
	@GetMapping("/changePassword")
	public String showChangePasswordForm() {
	    return "views/user/changePasswd";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("currentPassword") String currentPassword,
	                             @RequestParam("newPassword") String newPassword,
	                             @RequestParam("confirmPassword") String confirmPassword,
	                             @AuthenticationPrincipal PrincipalDetails principal,
	                             Model model) {

	    Long userNum = principal.getUserVO().getUser_num();
	    UserVO dbUser = userService.selectUser(userNum);
	    String dbPass = dbUser.getPasswd();

	    // 현재 비밀번호 검증
	    if (!passwordEncoder.matches(currentPassword, dbPass)) {
	        model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
	        return "views/user/changePasswd";
	    }

	    // 새 비밀번호 일치 확인
	    if (!newPassword.equals(confirmPassword)) {
	        model.addAttribute("error", "새 비밀번호가 일치하지 않습니다.");
	        return "views/user/changePasswd";
	    }

	    dbUser.setPasswd(passwordEncoder.encode(newPassword));
	    userService.updateUserPassword(dbUser);

	    model.addAttribute("accessTitle", "비밀번호 변경 완료");
	    model.addAttribute("accessMsg", "비밀번호가 성공적으로 변경되었습니다.");
	    model.addAttribute("accessBtn", "마이페이지");
	    model.addAttribute("accessUrl", "/user/myPage");

	    return "views/common/resultView";
	}
	
	// 자격증 사진처리
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/certView")
	public String getCertificate(long user_num,HttpServletRequest request,Model model) {
	    UserVO userVO = userService.selectRealtor(user_num); 
	    if (userVO == null || userVO.getRealtorDetail() == null || userVO.getRealtorDetail().getCertificate_name() == null) {
	        getBasicProfileImage(request, model);
	    } else {
	    	model.addAttribute("imageFile", userVO.getRealtorDetail().getCertificate());
	    	model.addAttribute("filename", userVO.getRealtorDetail().getCertificate_name());
	    }
	    return "imageView";
	}
	
	// 비밀번호 찾기
	@GetMapping("/sendPassword")
	public String sendPasswordForm() {
		return "views/user/userFindPassword";
	}
	
	// 회원 탈퇴 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete")
	public String formDelete() {
		return "views/user/userDelete";
	}
	
	// 회원 탈퇴
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete")
	public String submitDelete(
			@Valid UserVO userVO,BindingResult result,
			@AuthenticationPrincipal PrincipalDetails principal,
			HttpServletRequest request,	HttpServletResponse response,
			Model model) {
		log.debug("<<회원 탈퇴>> : {}", userVO);
		
		// id,passwd 필드의 에러만 체크
		if(result.hasFieldErrors("id") || result.hasFieldErrors("passwd")) {
			ValidationUtil.printErrorFields(result);
			return formDelete();
		}
		
		UserVO db_user = userService.selectUser(principal.getUserVO().getUser_num());
		// 비밀번호 일치 여부 체크
		try {
			if(userVO.getId().equals(db_user.getId()) && passwordEncoder.matches(userVO.getPasswd(), db_user.getPasswd())) { // 일치
				// 인증 성공, 회원정보 삭제
				userService.deleteUser(principal.getUserVO().getUser_num());
				// 로그아웃
				new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
				
				model.addAttribute("accessTitle", "회원탈퇴");
				model.addAttribute("accessMsg", "회원 탈퇴를 완료했습니다.");
				model.addAttribute("accessBtn", "홈으로");
				model.addAttribute("accessUrl", request.getContextPath()+"/main/main");

				return "views/common/resultView";
			}
			// 인증 실패
			throw new AuthCheckException();
		}catch(AuthCheckException e) {
			result.reject("invalidIdOrPassword");
			return formDelete();
		}
	}
	
	
}