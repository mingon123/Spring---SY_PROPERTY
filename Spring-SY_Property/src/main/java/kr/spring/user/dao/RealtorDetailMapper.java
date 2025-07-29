package kr.spring.user.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.user.vo.RealtorDetailVO;

@Mapper
public interface RealtorDetailMapper {
	public void insertRealtorDetail(RealtorDetailVO realtor);
	public void updateRealtorDetail(RealtorDetailVO  realtor);
	@Delete("DELETE FROM realtor_detail WHERE realtor_num=#{realtor_num}")
	public void deleteRealtorDetail(Long realtor_num);
	
	// 프로필 이미지 업데이트
	@Update("UPDATE realtor_detail SET photo=#{photo},photo_name=#{photo_name} WHERE realtor_num=#{realtor_num}")
	public void updateRealtorProfile(RealtorDetailVO  realtor);
	// 자격증 이미지 업데이트
	@Update("UPDATE realtor_detail SET certificate=#{certificate},certificate_name=#{certificate_name} WHERE realtor_num=#{realtor_num}")
	public void updateCertificate(RealtorDetailVO realtor);
	@Update("UPDATE realtor_detail SET valid_date = SYSDATE + 365 WHERE realtor_num=#{realtor_num}")
	public void updateValidDate(Long realtor_num);
	@Update("UPDATE realtor_detail SET valid_date = NULL WHERE realtor_num=#{realtor_num}")
	public void updateNoneValidDate(Long realtor_num);
	@Select("SELECT COUNT(*) FROM realtor_detail WHERE realtor_num=#{realtor_num}")
	public long countByUserNum(Long realtor_num);
}
