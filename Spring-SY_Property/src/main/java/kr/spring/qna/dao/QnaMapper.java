package kr.spring.qna.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.qna.vo.QnaVO;

@Mapper
public interface QnaMapper {

	public List<QnaVO> selectList(Map<String,Object> map);
	public Integer selectRowCount(Map<String,Object> map);
	public void insertQna(QnaVO qna);
	public QnaVO selectQna(Long qna_num);
	public void updateQna(QnaVO qna);
	public void deleteQna(Long qna_num);
	public void deletePhoto(Long qna_num);

	public void updateAnswer(QnaVO qna);

	// 관리자용 전체 게시글 수 조회
    public int selectRowCountAll();

    // 관리자용 전체 게시글 목록 조회 (페이징용 파라미터 포함)
    public List<QnaVO> selectListAll(Map<String,Object> map);
}










