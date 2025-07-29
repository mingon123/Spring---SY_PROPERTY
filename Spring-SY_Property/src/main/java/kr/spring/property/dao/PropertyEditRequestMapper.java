package kr.spring.property.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.property.vo.PropertyEditRequestVO;

@Mapper
public interface PropertyEditRequestMapper {
	 
	    void insertRequest(PropertyEditRequestVO vo);
	    List<PropertyEditRequestVO> findByPropertyId(Long propertyId);
	    List<PropertyEditRequestVO> getRequestsRealtor(Long userNum); // 추가됨
	    int getRequestCountRealtor(Long userNum); // 개수 조회
	    List<PropertyEditRequestVO> getRequestsPage(Map<String, Object> map); // 페이징 목록 조회
}
