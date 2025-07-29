package kr.spring.property.service;

import java.util.List;

import kr.spring.property.vo.PropertyEditRequestVO;
import kr.spring.property.vo.PropertyVO;

public interface PropertyEditRequestService {

	 void submitEditRequest(PropertyEditRequestVO vo);
	 List<PropertyEditRequestVO> getRequestsByPropertyId(Long propertyId);
	 PropertyVO getPropertyDetail(Long propertyId);
	 List<PropertyEditRequestVO> getRequestsRealtor(Long userNum);
	 int getRequestCountRealtor(Long userNum);
	List<PropertyEditRequestVO> getRequestsPage(Long userNum, int startRow, int endRow); 
}
