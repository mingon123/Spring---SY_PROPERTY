package kr.spring.user.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/*
로그인의 진행이 완료되면 Security Session(Security ContextHolder)이 만들어짐.
Authentication 타입 객체가 생성되고 그 안에 User 정보가 담기게 되는데 이때 UserDetails 타입 객체가 User 정보를 포함하게 됨.
*/

@Data // 만들 수 있는 요소들 (hashcode, toString 등 모두 만듦)
@Slf4j // 로그 찍기위한 클래스
public class PrincipalDetails implements UserDetails, OAuth2User{
	private UserVO userVO;
	private Map<String, Object> attributes;
	
	public PrincipalDetails(UserVO userVO) {
		this.userVO = userVO;
	}
	
	public PrincipalDetails(UserVO userVO, Map<String, Object> attributes) {
		this.userVO = userVO;
		this.attributes = attributes;
	}
	
	/*
	사용자의 권한 목록을 반환
	스프링 시큐리티에서 해당 사용자의 권한을 처리할 때 사용
	*/
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<GrantedAuthority>();
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				log.debug("userVO : " + userVO);
				return userVO.getAuthority();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		String passwd = userVO.getPasswd();
		return passwd != null ? passwd : "";
	}

	// 사용자 아이디(username) 반환
	@Override
	public String getUsername() {
//		return userVO.getId();
		if(userVO.getNick_name()==null) return userVO.getId();
		return userVO.getNick_name();
	}

	/*
	계정이 만료되지 않았는지 여부를 반환
	true일 경우 계정이 만료되지 않음을 의미
	*/
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	/*
	계정이 잠겨있지 않은지 여부를 반환
	true일 경우 계정이 잠기지 않았음을 의미
	*/
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	/*
	사용자 자격 증명이 만료되지 않았는지 여부 반환
	(해당 계정의 비밀번호가 1년이 지났는지? 등)
	*/
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/*
	계정이 활성되어 있는지 여부를 반환
	true일 경우 활성 사용자로 간주
	*/
	@Override
	public boolean isEnabled(){
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		if (attributes == null || attributes.get("id") == null) {
			return userVO.getId();
		}
		return String.valueOf(attributes.get("id"));
	}
	
}
