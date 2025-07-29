package kr.spring.tip.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.tip.dao.TipMapper;
import kr.spring.tip.vo.TipVO;

@Service
@Transactional
public class TipServiceImpl implements TipService{
	
	@Autowired
	private TipMapper tipMapper;

	@Override
	public List<TipVO> selectList(Map<String, Object> map) {
		
		return tipMapper.selectList(map);
	}

	@Override
	public Integer selectRowCount(Map<String, Object> map) {
		
		return tipMapper.selectRowCount(map);
	}

	@Override
	public void insertTip(TipVO tip) {
		tipMapper.insertTip(tip);
	}

	@Override
	public TipVO selectTip(Long tip_num) {
	
		return tipMapper.selectTip(tip_num);
	}

	@Override
	public void updateTip(TipVO tip) {
		tipMapper.updateTip(tip);
	}

	@Override
	public void deleteTip(Long tip_num) {
		tipMapper.deleteTip(tip_num);
	}

	@Override
	public void deletePhoto(Long tip_num) {
		tipMapper.deletePhoto(tip_num);
	}

	@Override
	public List<TipVO> selectListUser(Map<String, Object> map) {
		return tipMapper.selectListUser(map);
	}

	
}

	





