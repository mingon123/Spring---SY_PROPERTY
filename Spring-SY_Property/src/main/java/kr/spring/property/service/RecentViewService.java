package kr.spring.property.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.spring.property.vo.PropertyVO;


public interface RecentViewService {

	void saveRecentView(Long user_num, Long property_id);
	List <PropertyVO> getRecentList(Long user_num);
	
	
}
