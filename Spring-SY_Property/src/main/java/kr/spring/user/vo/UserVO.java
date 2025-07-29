package kr.spring.user.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserVO {
	private long user_num;
	@Pattern(regexp="^[A-Za-z0-9]{4,14}$")
	private String id;
	private String nick_name;
	private String authority;
	private int report_count;
	@Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$")
	private String passwd;
	
	@Valid
	private UserDetailVO userDetail;
	@Valid
	private RealtorDetailVO realtorDetail;
	
	// 회원:USER, 중개사:REALTOR
	private String user_type;
	// 소설 로그인 계정 여부 0:일반,1:소셜
	private int oauth;
	
	// 비밀번호 변경시 현재 비밀번호를 저장하는 용도로 사용
	@Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$")
	private String now_passwd;
	
	// 답글(대댓글) 작성시 부모글 아이디/별명
	private String parent_id;
	private String pnick_name;
	
	public String getParentName() {
		if(pnick_name==null) return parent_id;
		return pnick_name;
	}
	
	// 별명이 미등록되 있으면 id 반환, 별명이 등록되어 있으면 별명 반환
	public String getUserName() {
		if(nick_name==null) return id;
		return nick_name;
	}
	
	// 비밀번호 일치 여부 체크
	public boolean isCheckedPassword(String userPasswd) {
		if(getAuthorityOrdinal() > 1 && getPasswd() != null && getPasswd().equals(userPasswd)) {
			return true;
		}
		return false;
	}
	
	// 회원 등급 체크
	public int getAuthorityOrdinal() {
		if(authority == null) return -1;
		
		if(authority.equals(UserRole.INACTIVE.getValue())) {
			return UserRole.INACTIVE.ordinal(); // 탈퇴 : 0
		}else if(authority.equals(UserRole.SUSPENDED.getValue())) {
			return UserRole.SUSPENDED.ordinal(); // 정지 : 1
		}else if(authority.equals(UserRole.USER.getValue())) {
			return UserRole.USER.ordinal(); // 일반회원 : 2
		}else if(authority.equals(UserRole.N_REALTOR.getValue())) {
			return UserRole.N_REALTOR.ordinal(); // 미인증 중개사 : 3
		}else if(authority.equals(UserRole.REALTOR.getValue())) {
			return UserRole.REALTOR.ordinal(); // 중개사 : 4
		}else if(authority.equals(UserRole.ADMIN.getValue())) {
			return UserRole.ADMIN.ordinal(); // 관리자 : 5
		}else {
			return -1;
		}
	}

}
