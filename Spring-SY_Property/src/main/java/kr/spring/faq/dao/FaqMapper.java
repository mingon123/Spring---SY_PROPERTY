package kr.spring.faq.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.faq.vo.FaqVO;


@Mapper
public interface FaqMapper {

	public List<FaqVO> selectList(Map<String,Object> map);
	public Integer selectRowCount(Map<String,Object> map);
	public void insertFaq(FaqVO faq);
	public FaqVO selectFaq(Long faq_num);
	public void updateFaq(FaqVO faq);
	public void deleteFaq(Long faq_num);

}









