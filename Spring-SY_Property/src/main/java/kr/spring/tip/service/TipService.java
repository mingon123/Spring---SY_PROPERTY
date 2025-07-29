package kr.spring.tip.service;

import java.util.List;
import java.util.Map;

import kr.spring.tip.vo.TipVO;

public interface TipService {
	
	//관리자 목록
	public List<TipVO> selectList(Map<String,Object> map);
	
	//관리자 목록
	public List<TipVO> selectListUser(Map<String,Object> map);
		
	//게시글 수
	public Integer selectRowCount(Map<String,Object> map);
	//등록
	public void insertTip(TipVO tip);
	//상세
	public TipVO selectTip(Long tip_num);
	//수정
	public void updateTip(TipVO tip);
	//삭제
	public void deleteTip(Long tip_num);
	//사진삭제
	public void deletePhoto(Long tip_num);
}





