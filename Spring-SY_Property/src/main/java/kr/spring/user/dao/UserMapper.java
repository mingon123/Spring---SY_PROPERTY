package kr.spring.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.user.vo.UserVO;

@Mapper
public interface UserMapper {
	// 회원관리 - 일반회원
	@Select("SELECT users_seq.nextval FROM dual")
	public Long selectUserNum();
	@Insert("INSERT INTO users (user_num,id,passwd,authority,user_type) VALUES (#{user_num},#{id},#{passwd},'ROLE_USER','USER')")
	public void insertUser(UserVO user);
	@Insert("INSERT INTO users (user_num,id,passwd,authority,user_type) VALUES (#{user_num},#{id},#{passwd},'ROLE_N_REALTOR','REALTOR')")
	public void insertRealtor(UserVO user);
	public UserVO selectUser(Long user_num);
	public UserVO selectRealtor(Long user_num);
	@Insert("INSERT INTO users (user_num,id,passwd,authority,user_type,oauth,report_count,nick_name) "
			+ "VALUES (#{user_num},#{id},#{passwd},#{authority},#{user_type},#{oauth},#{report_count},#{nick_name})")
	public void insertSocialUser(UserVO user);
	
	
	UserVO selectUserByNum(Long user_num); 

	// 로그인 처리
	@Select("SELECT * FROM users WHERE id=#{id}")
	public UserVO selectCheckUser(String id);
	@Select("SELECT * FROM users WHERE id=#{id}")
	public UserVO selecChecktId(String id);
	@Select("SELECT * FROM users WHERE nick_name=#{nick_name}")
	public UserVO selectCheckNickName(String nick_name);
	@Update("UPDATE users SET nick_name=#{nick_name} WHERE user_num=#{user_num}")
	public void updateUser(UserVO user);
	@Update("UPDATE users SET authority='ROLE_INACTIVE' WHERE user_num=#{user_num}")
	public void deleteUser(Long user_num);
	// 비밀번호 변경
	@Update("UPDATE users SET passwd=#{passwd} WHERE user_num=#{user_num}")
	public void updateUserPassword(UserVO user);
	// 비밀번호 찾기
	@Update("UPDATE users SET passwd=#{passwd} WHERE user_num=#{user_num}")
	public void updateRandomPassword(UserVO user);
	public UserVO selectIdEmailCheckUser(String id);
	// 자동 로그인 해제
	@Delete("DELETE FROM persistent_logins WHERE user_num=#{user_num}")
	public void deleteRememberMe(Long user_num);
	// 이메일로 userVO 반환
	public UserVO selectUserByEmail(String email);
	public UserVO selectRealtorByEmail(String email);
	@Select("SELECT report_count FROM users WHERE user_num=#{user_num}")
	public int getReportCount(Long user_num);
	@Update("UPDATE users SET report_count=#{report_count} WHERE user_num=#{user_num}")
	public void updateReportCount(Long user_num, int report_count);
	
	// 회원관리 - 관리자
	public Integer selectRowCount(Map<String, Object> map);
	public List<UserVO> selectList(Map<String, Object> map);
	public Integer selectRealtorRowCount(Map<String, Object> map);
	public List<UserVO> selectRealtorList(Map<String, Object> map);
	@Update("UPDATE users SET authority=#{authority} WHERE user_num=#{user_num}")
	public void updateByAdmin(UserVO user);
	// 중개사 인증 처리
	@Update("UPDATE users SET authority='ROLE_REALTOR' WHERE user_num=#{user_num}")
	public void updateAuthorityToRealtor(Long user_num);
}
