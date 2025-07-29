package kr.spring.user.service;

import java.util.List;
import java.util.Map;

import kr.spring.user.vo.RealtorDetailVO;
import kr.spring.user.vo.UserDetailVO;
import kr.spring.user.vo.UserVO;

public interface UserService {
	
    // ----------- users (기본 회원) -----------
	public Long selectUserNum();
	public void insertUser(UserVO user);
	public void insertRealtor(UserVO user);
	public UserVO selectUser(Long user_num);
	public UserVO selectRealtor(Long user_num);
	public void insertSocialUser(UserVO user);
	// 로그인 처리
	public UserVO selectCheckUser(String id);
	public UserVO selectCheckId(String id);
	public UserVO selectCheckNickName(String nick_name);
	public void updateUser(UserVO user);
	public void deleteUser(Long user_num);
	
	
	UserVO selectUserByNum(Long user_num);
	// 비밀번호 변경
	public void updateUserPassword(UserVO user);
	// 비밀번호 찾기
	public void updateRandomPassword(UserVO user);
	public UserVO selectIdEmailCheckUser(String id);
	// 이메일로 userVO 반환
	public UserVO selectUserByEmail(String email);
	public UserVO selectRealtorByEmail(String email);
	// 신고
	public int getReportCount(Long user_num);
	public void updateReportCount(Long user_num, int report_count);
	
	// 미인증 중개사
	public void saveOrUpdateRealtorDetail(UserVO userVO);
	
	
    // ----------- users_detail (일반회원 상세) -----------
	public void insertUserDetail(UserDetailVO user);
	public void updateUserDetail(UserDetailVO user);
	// 프로필 이미지 업데이트
	public void updateUserProfile(UserDetailVO user);

    // ----------- realtor_detail (중개사 상세) -----------
	public void insertRealtorDetail(RealtorDetailVO realtor);
	public void updateRealtorDetail(RealtorDetailVO  realtor);
	// 프로필 이미지 업데이트
	public void updateRealtorProfile(RealtorDetailVO  realtor);
	// 자격증 이미지 업데이트
	public void updateCertificate(RealtorDetailVO realtor);
	public void updateValidDate(Long realtor_num);
	public void updateNoneValidDate(Long realtor_num);
	
    // ----------- 관리자/검색/목록 -----------
	public Integer selectRowCount(Map<String, Object> map);
	public List<UserVO> selectList(Map<String, Object> map);
	public Integer selectRealtorRowCount(Map<String, Object> map);
	public List<UserVO> selectRealtorList(Map<String, Object> map);
	public void updateByAdmin(UserVO user);
	// 중개사 인증 처리
	public void updateAuthorityToRealtor(Long user_num);
	
}
