package kr.spring.property.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.spring.property.dao.RecentViewMapper;
import kr.spring.property.vo.PropertyVO;
import kr.spring.property.vo.RecentViewVO;

@Service
public class RecentViewServiceImpl implements RecentViewService{

	@Autowired
	private RecentViewMapper recentViewMapper;
	
	@Override
	public void saveRecentView(Long userNum, Long propertyId) {
		
		// 봤던 매물인지 확인하는 변수선언
		RecentViewVO existSeeView = recentViewMapper.selectUserProperty(userNum, propertyId);
		
	   if(existSeeView != null)
	   {
	    	// 이미 본 매물이면 시간만 갱신하  
		    recentViewMapper.updateView(userNum, propertyId);
		    
	   } else {
		   
		    // 처음 보는 매물이면 -> 새로 insert 
		   RecentViewVO view = new RecentViewVO();
		   view.setUser_num(userNum);
		   view.setProperty_id(propertyId);
		   recentViewMapper.insertView(view);
		   

	   }
	   
	   // 본 기록이 10개가 넘어가면 삭
	   recentViewMapper.deleteOldView(userNum, 10);
	
		
	}

	@Override
	public List<PropertyVO> getRecentList(Long UserNum) {
		// TODO Auto-generated method stub
		return recentViewMapper.selectRecentProperties(UserNum);
	}

}
