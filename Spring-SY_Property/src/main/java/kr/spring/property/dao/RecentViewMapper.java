package kr.spring.property.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.property.vo.PropertyVO;
import kr.spring.property.vo.RecentViewVO;

@Mapper
public interface RecentViewMapper {

	RecentViewVO selectUserProperty(Long user_num, Long property_id);
	void insertView(RecentViewVO view);
	void updateView(Long user_num, Long property_id);
	void deleteOldView(Long user_num, int limit);
	
	List<PropertyVO> selectRecentProperties(Long user_num);
	
}
