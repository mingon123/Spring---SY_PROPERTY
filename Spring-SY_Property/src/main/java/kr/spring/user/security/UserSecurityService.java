package kr.spring.user.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.spring.user.service.UserService;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.user.vo.UserRole;
import kr.spring.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// 클래스 내에서 final 또는 @NonNull로 선언된 필드에 대해 생성자를 자동으로 생성(의존성 주입) 
@RequiredArgsConstructor
// 로그인 시 사용자 정보를 조회하고, 이를 기반으로 인증(Authentication)을 수행하는 데 사용
@Service
public class UserSecurityService implements UserDetailsService{
	// @RequiredArgsConstructor에 의해 의존성 주입됨
	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		log.debug("[Spring Security Login Check 1] UserSecurityService 실행");
		log.debug("[Spring Security Login Check 1] 로그인 아이디 : " + id);
		UserVO user = userService.selectCheckUser(id);
		if(user==null || user.getAuthority().equals(UserRole.INACTIVE.getValue())) {
			log.debug("[Spring Security Login Check 1] 로그인 아이디가 없거나 탈퇴회원");
			throw new UsernameNotFoundException("UserNotFound");
		}
		
		if (user.getAuthority().equals(UserRole.SUSPENDED.getValue())) {
			log.debug("[Spring Security Login Check 1] 정지회원 로그인 시도");
			throw new org.springframework.security.authentication.BadCredentialsException("정지회원");
		}
		
		return new PrincipalDetails(user);
	}


	
}
