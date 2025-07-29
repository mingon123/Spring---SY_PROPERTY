package kr.spring.user.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.user.service.UserService;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.user.vo.RealtorDetailVO;
import kr.spring.user.vo.UserDetailVO;
import kr.spring.user.vo.UserRole;
import kr.spring.user.vo.UserVO;
import kr.spring.util.PagingUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/user")
public class UserAdminController {
	@Autowired
	private UserService userService;
	
	// 회원목록
	@GetMapping("/admin_list")
	public String getList(@RequestParam(defaultValue="1") int pageNum,String keyfield,String keyword,
			@RequestParam(required=false) List<String> authority,Model model) {		
		Map<String, Object> map = new HashMap<String, Object>();
		
		// 전화번호 검색 시 '-' 제거
		if ("4".equals(keyfield)) {
		    keyword = keyword.replaceAll("-", "");
		}
		
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		map.put("authorityList", authority);
		
		// 전체/검색 레코드수
		int count = userService.selectRowCount(map);
		log.debug("<<회원목록 - count>> : {}", count);
		
		// 페이지 처리
		PagingUtil page = new PagingUtil(keyfield,keyword,pageNum,count,10,10,"admin_list");
		
		List<UserVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());

			list = userService.selectList(map);
			
			for(UserVO user : list) {
				if(user.getUserDetail() == null) {
					user.setUserDetail(new UserDetailVO());
				}
			}
		}
		
		List<UserRole> roles = UserRole.getGeneralRoles();
		model.addAttribute("authorityRows", partition(roles, 4));
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		model.addAttribute("keyfield", keyfield);
		model.addAttribute("keyword", keyword);
		model.addAttribute("authority", authority);
		
		return "views/user/admin_list";
	}
	
    // 회원 등급 정렬
    private List<List<UserRole>> partition(List<UserRole> input, int size) {
        List<List<UserRole>> result = new ArrayList<>();
        for (int i = 0; i < input.size(); i += size) {
            result.add(input.subList(i, Math.min(i + size, input.size())));
        }
        return result;
    }
	
	// 회원정보 수정 폼 호출
	@GetMapping("/admin_update")
	public String form(long user_num, Model model) {
		UserVO userVO = userService.selectUser(user_num);
	    if (userVO == null) userVO = new UserVO();
	    if (userVO.getUserDetail() == null) userVO.setUserDetail(new UserDetailVO());
	    
		model.addAttribute("userVO", userVO);
		
		return "views/user/admin_modify";
	}
	
	// 회원정보 수정 처리
	@PostMapping("/admin_update")
	public String submit(@Valid UserVO userVO,BindingResult result, 
			Model model,HttpServletRequest request,@AuthenticationPrincipal PrincipalDetails principal) {
		log.debug("<<회원권한 수정>> : {}", userVO);
		
		if(result.hasErrors()) {
			ValidationUtil.printErrorFields(result);
			
		    UserVO dbUser = userService.selectUser(userVO.getUser_num());
		    userVO.setId(dbUser.getId());
		    if (!dbUser.getAuthority().equals(userVO.getAuthority())) {
		        userService.updateByAdmin(userVO);

				model.addAttribute("accessTitle", "회원등급 수정");
				model.addAttribute("accessMsg", "회원등급 수정 완료");
				model.addAttribute("accessUrl", request.getContextPath()+"/user/admin_realtorUpdate?user_num="+userVO.getUser_num());
				model.addAttribute("accessBtn", "수정페이지");
				model.addAttribute("accessUrl2", request.getContextPath()+"/user/admin_realtorList");
				model.addAttribute("accessBtn2", "회원목록");
		        return "views/common/resultView2";
		    }
			
			return "views/user/admin_modify";
		}
		
		// 회원권한 수정
		userService.updateByAdmin(userVO);
		userService.updateUser(userVO);
		
		int inputCount = userVO.getReport_count();
		userService.updateReportCount(userVO.getUser_num(), inputCount);
		
		if (userVO.getUserDetail() != null) {
		    UserVO dbVO = userService.selectUser(userVO.getUser_num());
		    userVO.getUserDetail().setUser_num(userVO.getUser_num());
		    if (dbVO == null || dbVO.getUserDetail() == null) {
		        userService.insertUserDetail(userVO.getUserDetail());
		    } else {
		        userService.updateUserDetail(userVO.getUserDetail());
		    }
		}
		
		// View에 표시할 메시지
		model.addAttribute("accessTitle", "회원정보 수정");
		model.addAttribute("accessMsg", "회원정보 수정 완료");
		model.addAttribute("accessUrl", request.getContextPath()+"/user/admin_update?user_num="+userVO.getUser_num());
		model.addAttribute("accessBtn", "수정페이지");
		model.addAttribute("accessUrl2", request.getContextPath()+"/user/admin_list");
		model.addAttribute("accessBtn2", "회원목록");
		
		return "views/common/resultView2";
	}
	

	// 중개사목록
	@GetMapping("/admin_realtorList")
	public String getRealtorList(@RequestParam(defaultValue="1") int pageNum,String keyfield,String keyword,
			@RequestParam(required=false) List<String> authority,Model model) {		
		Map<String, Object> map = new HashMap<String, Object>();
		
		// 전화번호 검색 시 '-' 제거
		if ("4".equals(keyfield)) {
		    keyword = keyword.replaceAll("-", "");
		}
		
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		map.put("authorityList", authority);
		
		// 전체/검색 레코드수
		int count = userService.selectRealtorRowCount(map);
		log.debug("<<중개사목록 - count>> : {}", count);
		
		// 페이지 처리
		PagingUtil page = new PagingUtil(keyfield,keyword,pageNum,count,10,10,"admin_realtorList");
		
		List<UserVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());

			list = userService.selectRealtorList(map);
			
			for(UserVO user : list) {
				if(user.getRealtorDetail() == null) {
					user.setRealtorDetail(new RealtorDetailVO());
				}
			}
		}
		
		List<UserRole> roles = UserRole.getRealtorRoles();
		model.addAttribute("authorityRows", partition(roles, 3));
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		model.addAttribute("keyfield", keyfield);
		model.addAttribute("keyword", keyword);
		model.addAttribute("authority", authority);
		
		return "views/user/admin_realtorList";
	}
	
	// 중개사정보 수정 폼 호출
	@GetMapping("/admin_realtorUpdate")
	public String realtorForm(long user_num, Model model) {
		UserVO userVO = userService.selectRealtor(user_num);
	    if (userVO == null) userVO = new UserVO();
	    if (userVO.getRealtorDetail() == null) userVO.setRealtorDetail(new RealtorDetailVO());
	    
		model.addAttribute("userVO", userVO);
		
		return "views/user/admin_realtorModify";
	}
	
	// 중개사정보 수정 처리
	@PostMapping("/admin_realtorUpdate")
	public String realtorSubmit(@Valid UserVO userVO,BindingResult result, 
			Model model,HttpServletRequest request,@AuthenticationPrincipal PrincipalDetails principal) {
		log.debug("<<중개사정보 수정>> : {}", userVO);
		
	    if ("ROLE_REALTOR".equals(userVO.getAuthority())) {
	        UserVO dbVO = userService.selectRealtor(userVO.getUser_num());
	        if (dbVO == null || dbVO.getRealtorDetail() == null || dbVO.getRealtorDetail().getCertificate_name() == null) {
	            model.addAttribute("accessTitle", "권한 수정 불가");
	            model.addAttribute("accessMsg", "중개사 정보가 등록되지 않아 권한을<br>'중개사'로 변경할 수 없습니다.");
	            model.addAttribute("accessUrl", request.getContextPath() + "/user/admin_realtorUpdate?user_num=" + userVO.getUser_num());
	            model.addAttribute("accessBtn", "수정 페이지로");
	            return "views/common/resultView";
	        }
	    }
		
		if (result.hasErrors()) {
			ValidationUtil.printErrorFields(result);
			
		    UserVO dbUser = userService.selectRealtor(userVO.getUser_num());
		    userVO.setId(dbUser.getId());
		    if (!dbUser.getAuthority().equals(userVO.getAuthority())) {
		        userService.updateByAdmin(userVO);

				model.addAttribute("accessTitle", "중개사등급 수정");
				model.addAttribute("accessMsg", "중개사등급 수정 완료");
				model.addAttribute("accessUrl", request.getContextPath()+"/user/admin_realtorUpdate?user_num="+userVO.getUser_num());
				model.addAttribute("accessBtn", "수정페이지");
				model.addAttribute("accessUrl2", request.getContextPath()+"/user/admin_realtorList");
				model.addAttribute("accessBtn2", "중개사목록");
		        return "views/common/resultView2";
		    }
		    return "views/user/admin_realtorModify";
		}
		
		// 회원등급,닉네임 수정
		userService.updateByAdmin(userVO);
		userService.updateUser(userVO);

		if ("ROLE_REALTOR".equals(userVO.getAuthority())) {
			userService.updateValidDate(userVO.getUser_num());
		}else {
			userService.updateNoneValidDate(userVO.getUser_num());
		}
		
		if (userVO.getRealtorDetail() != null) {
		    UserVO dbVO = userService.selectRealtor(userVO.getUser_num());
		    userVO.getRealtorDetail().setRealtor_num(userVO.getUser_num());
		    if (dbVO == null || dbVO.getRealtorDetail() == null) {
		        userService.insertRealtorDetail(userVO.getRealtorDetail());
		    } else {
		        userService.updateRealtorDetail(userVO.getRealtorDetail());
		    }
		}
		
		// View에 표시할 메시지
		model.addAttribute("accessTitle", "중개사정보 수정");
		model.addAttribute("accessMsg", "중개사정보 수정 완료");
		model.addAttribute("accessUrl", request.getContextPath()+"/user/admin_realtorUpdate?user_num="+userVO.getUser_num());
		model.addAttribute("accessBtn", "수정페이지");
		model.addAttribute("accessUrl2", request.getContextPath()+"/user/admin_realtorList");
		model.addAttribute("accessBtn2", "중개사목록");
		
		return "views/common/resultView2";
	}
	

	
	
	
}
