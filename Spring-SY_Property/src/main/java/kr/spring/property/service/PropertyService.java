package kr.spring.property.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import jakarta.validation.Valid;
import kr.spring.property.vo.PropertyFavVO;
import kr.spring.property.vo.PropertyVO;

public interface PropertyService {
	public List<PropertyVO> getPropertiesList();
	public List<PropertyVO> getPropertiesListByCategory(Map<String, Object> map);
	
	//좋아요
	public PropertyFavVO selectFav(PropertyFavVO fav);
	public void insertFav(PropertyFavVO fav);
	public void deleteFav(PropertyFavVO fav);

	
	List<Long> getFavPropertyId(Long userNum);

	public List<PropertyVO> getFavPropertyList(Long userNum);

	// 페이징 처리용(메소드 오버로딩)
	List<PropertyVO> getFavPropertyListPaging(Long userNum, int start, int end);
	public int getFavCount(Long userNum);

	
	// PropertyService.java
	List<PropertyVO> getPropertyRealtor(long realtorNum);
	List<PropertyVO> getFilterProperty(Map<String, String> filters);

	public void insertProperty(@Valid PropertyVO propertyVO);
	
	public PropertyVO getPropertyDetail(Long propertyId);

	// 관리자
	public int selectRowCount();
	public List<PropertyVO> selectList(Map<String, Object> map);
	public void updateProperty(PropertyVO propertyVO);
	public void deleteProperty(Long propertyId);
	
	// 중개사가 관리하는 매물 페이징 처리용
	int getPropertyCountRealtor(Long realtorNum);
	List<PropertyVO> getPropertyRealtorPage(Long realtorNum, int startRow, int endRow);
	
    List<PropertyVO> searchByCategory(String category);

}	
