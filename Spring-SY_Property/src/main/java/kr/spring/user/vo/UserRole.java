package kr.spring.user.vo;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum UserRole {
	// 탈퇴,정지,회원,미인증중개사,중개사,관리자
	INACTIVE("ROLE_INACTIVE", "탈퇴"), 
	SUSPENDED("ROLE_SUSPENDED", "정지"), 
	USER("ROLE_USER", "일반"), 
	REALTOR("ROLE_REALTOR", "중개사"), 
	N_REALTOR("ROLE_N_REALTOR", "미인증중개사"), 
	N_REALTOR_CERT("ROLE_N_REALTOR_CERT", "인증대기"), // 필터링에서만 사용될 라벨
	ADMIN("ROLE_ADMIN", "관리자");
	
	private final String value;
	private final String label;
	
    UserRole(String value, String label) {
        this.value = value;
        this.label = label;
    }
    
    public static List<UserRole> getAllRoles() {
        return Arrays.asList(values());
    }

    public static List<UserRole> getGeneralRoles() {
        return Arrays.asList(USER, SUSPENDED, INACTIVE, ADMIN);
    }

    public static List<UserRole> getRealtorRoles() {
    	return Arrays.asList(N_REALTOR_CERT, REALTOR, N_REALTOR, SUSPENDED, INACTIVE);
    }
    
}
