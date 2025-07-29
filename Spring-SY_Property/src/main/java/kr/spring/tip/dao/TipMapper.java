package kr.spring.tip.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.tip.vo.TipVO;


@Mapper
public interface TipMapper {

	public List<TipVO> selectList(Map<String,Object> map);
	public List<TipVO> selectListUser(Map<String,Object> map);
	public Integer selectRowCount(Map<String,Object> map);
	public void insertTip(TipVO tip);
	public TipVO selectTip(Long tip_num);
	public void updateTip(TipVO tip);
	public void deleteTip(Long tip_num);
	public void deletePhoto(Long tip_num);

}









