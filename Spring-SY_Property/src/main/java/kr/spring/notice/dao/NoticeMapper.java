package kr.spring.notice.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.notice.vo.NoticeVO;

@Mapper
public interface NoticeMapper {

	public List<NoticeVO> selectList(Map<String,Object> map);
	public Integer selectRowCount(Map<String,Object> map);
	public void insertNotice(NoticeVO notice);
	public NoticeVO selectNotice(Long notice_num);
	public void updateNotice(NoticeVO notice);
	public void deleteNotice(Long notice_num);

}









