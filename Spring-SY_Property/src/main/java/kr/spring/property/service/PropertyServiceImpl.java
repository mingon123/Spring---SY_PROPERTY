package kr.spring.property.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import kr.spring.property.dao.PropertyMapper;
import kr.spring.property.vo.PropertyFavVO;
import kr.spring.property.vo.PropertyVO;


@Service
@Transactional
public class PropertyServiceImpl implements PropertyService{

	@Autowired
	private PropertyMapper propertyMapper;
	
	@Override
	public List<PropertyVO> getPropertiesList() {
		return propertyMapper.getPropertiesList();
	}

	@Override
	public List<PropertyVO> getPropertiesListByCategory(Map<String, Object> map) {
		return propertyMapper.getPropertiesListByCategory(map);
	}
	
	@Override
	public void insertProperty(@Valid PropertyVO propertyVO) {
		// TODO Auto-generated method stub
	
		  
		  propertyVO.setRoom_number(generateRoomNumber());
		  propertyMapper.insertProperty(propertyVO);
	}
    
	private String generateRoomNumber() {
	    // 오늘 날짜
	    String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());

	    // 오늘 날짜 기준으로 등록된 매물 수
	    int countToday = propertyMapper.countPropertyByDate(datePart);

	    // 일련번호 4자리 포맷팅
	    String serial = String.format("%04d", countToday + 1);

	    // 조합
	    return "R-" + datePart + "-" + serial;
	}
	
	@Override
	public List<PropertyVO> getPropertyRealtor(long realtorNum) {
	    return propertyMapper.getPropertyRealtor(realtorNum);
	}

	@Override
	public List<PropertyVO> getFilterProperty(Map<String, String> filters) {
		// TODO Auto-generated method stub
		return propertyMapper.getFilterProperty(filters);
	}
   

	@Override
	public PropertyVO getPropertyDetail(Long propertyId) {
		// TODO Auto-generated method stub
		 return propertyMapper.selectPropertyID(propertyId);
	}

	@Override
	public PropertyFavVO selectFav(PropertyFavVO fav) {
		return propertyMapper.selectFav(fav);
	}

	@Override
	public void insertFav(PropertyFavVO fav) {
		propertyMapper.insertFav(fav);
	}

	@Override
	public void deleteFav(PropertyFavVO fav) {
		propertyMapper.deleteFav(fav);
	}

	@Override
	public int selectRowCount() {
		return propertyMapper.selectRowCount();
	}

	@Override
	public List<PropertyVO> selectList(Map<String, Object> map) {
		return propertyMapper.selectList(map);
	}

	@Override
	public void updateProperty(PropertyVO propertyVO) {
		propertyMapper.updateProperty(propertyVO);
	}

	@Override
	public void deleteProperty(Long propertyId) {
		propertyMapper.deleteProperty(propertyId);
	}

	@Override
	public int getPropertyCountRealtor(Long realtorNum) {
		// TODO Auto-generated method stub
		return propertyMapper.getPropertyCountRealtor(realtorNum);
	}

	@Override
	public List<PropertyVO> getPropertyRealtorPage(Long realtorNum, int startRow, int endRow) {
		// TODO Auto-generated method stub
		return propertyMapper.getPropertyRealtorPage(realtorNum, startRow, endRow);
	}
	 
	   
	 @Override
	 public List<PropertyVO> searchByCategory(String category) {
	     return propertyMapper.searchByCategory(category);
	 }
	

	 
	 @Override
	 public List<Long> getFavPropertyId(Long userNum) {
	     return propertyMapper.selectFavPropertyId(userNum);
	 }

	 @Override
	 public List<PropertyVO> getFavPropertyList(Long userNum) {
	     return propertyMapper.selectFavPropertyList(userNum);
	 }

	 @Override
	 public List<PropertyVO> getFavPropertyListPaging(Long userNum, int start, int end) {
	     Map<String, Object> map = new HashMap<>();
	     map.put("userNum", userNum);
	     map.put("start", start);
	     map.put("end", end);
	     return propertyMapper.getFavPropertyListPaging(map);
	 }
	 @Override
	 public int getFavCount(Long userNum) {
	     return propertyMapper.getFavCount(userNum);
	 }


}










