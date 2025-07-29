package kr.spring.notice.service;

import java.util.List;
import java.util.Map;

import kr.spring.notice.vo.NoticeVO;

public interface NoticeService {
	
	//목록
	public List<NoticeVO> selectList(Map<String,Object> map);
	//게시글 수
	public Integer selectRowCount(Map<String,Object> map);
	//등록
	public void insertNotice(NoticeVO notice);
	//상세
	public NoticeVO selectNotice(Long notice_num);
	//수정
	public void updateNotice(NoticeVO notice);
	//삭제
	public void deleteNotice(Long notice_num);
}





