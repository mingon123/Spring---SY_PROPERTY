package kr.spring.property.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.property.dao.PropertyEditRequestMapper;
import kr.spring.property.dao.PropertyMapper;
import kr.spring.property.vo.PropertyEditRequestVO;
import kr.spring.property.vo.PropertyVO;

@Service
@Transactional
public class PropertyEditRequestServiceImpl implements PropertyEditRequestService {

    @Autowired
    private PropertyEditRequestMapper propertyEditRequestMapper;

    @Autowired
    private PropertyMapper propertyMapper;

    @Override
    public void submitEditRequest(PropertyEditRequestVO vo) {
        propertyEditRequestMapper.insertRequest(vo);
    }

    @Override
    public List<PropertyEditRequestVO> getRequestsByPropertyId(Long propertyId) {
        return propertyEditRequestMapper.findByPropertyId(propertyId);
    }

    @Override
    public PropertyVO getPropertyDetail(Long propertyId) {
        return propertyMapper.getPropertyDetail(propertyId);
    }

    @Override
    public List<PropertyEditRequestVO> getRequestsRealtor(Long userNum) {
        return propertyEditRequestMapper.getRequestsRealtor(userNum);
    }

	@Override
	public int getRequestCountRealtor(Long userNum) {
		// TODO Auto-generated method stub
		 return propertyEditRequestMapper.getRequestCountRealtor(userNum);
	}

	@Override
	public List<PropertyEditRequestVO> getRequestsPage(Long userNum, int startRow, int endRow) {
		    Map<String, Object> map = new HashMap<>();
		    map.put("userNum", userNum);
		    map.put("start", startRow);
		    map.put("end", endRow);
		    return propertyEditRequestMapper.getRequestsPage(map); 
	}
}

