package kr.spring.user.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import kr.spring.user.vo.UserDetailVO;

@Mapper
public interface UserDetailMapper {
	public void insertUserDetail(UserDetailVO user);
	public void updateUserDetail(UserDetailVO user);
	@Delete("DELETE FROM users_detail WHERE user_num=#{user_num}")
	public void deleteUserDetail(Long user_num);

	// 프로필 이미지 업데이트
	@Update("UPDATE users_detail SET photo=#{photo},photo_name=#{photo_name} WHERE user_num=#{user_num}")
	public void updateProfile(UserDetailVO user);
}
