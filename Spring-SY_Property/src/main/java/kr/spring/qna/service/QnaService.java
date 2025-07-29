package kr.spring.qna.service;

import java.util.List;
import java.util.Map;

import kr.spring.qna.vo.QnaVO;

public interface QnaService {
	
	// 사용자목록
	public List<QnaVO> selectList(Map<String,Object> map);
	// 게시글 수
	public Integer selectRowCount(Map<String,Object> map);
	// 등록
	public void insertQna(QnaVO qna);
	// 상세
	public QnaVO selectQna(Long qna_num);
	// 수정
	public void updateQna(QnaVO qna);
	// 삭제
	public void deleteQna(Long qna_num);
	// 사진삭제
	public void deletePhoto(Long qna_num);
	// 관리자 답변
	public void updateAnswer(QnaVO qna);
	
	// 관리자 전체 목록 조회
	public List<QnaVO> selectListAll(Map<String,Object> map);
	// 관리자 전체 게시글 수 조회
	public int selectRowCountAll();
}
