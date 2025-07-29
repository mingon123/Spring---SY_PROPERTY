package kr.spring.faq.service;

import java.util.List;
import java.util.Map;

import kr.spring.faq.vo.FaqVO;

public interface FaqService {
	
	//목록
	public List<FaqVO> selectList(Map<String,Object> map);
	//게시글 수
	public Integer selectRowCount(Map<String,Object> map);
	//등록
	public void insertFaq(FaqVO faq);
	//상세
	public FaqVO selectFaq(Long faq_num);
	//수정
	public void updateFaq(FaqVO faq);
	//삭제
	public void deleteFaq(Long faq_num);
}





