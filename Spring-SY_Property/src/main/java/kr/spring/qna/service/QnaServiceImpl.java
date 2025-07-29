package kr.spring.qna.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.qna.dao.QnaMapper;
import kr.spring.qna.vo.QnaVO;


@Service
@Transactional
public class QnaServiceImpl implements QnaService{
	
	@Autowired
	private QnaMapper qnaMapper;

	@Override
	public List<QnaVO> selectList(Map<String, Object> map) {
		
		return qnaMapper.selectList(map);
	}

	@Override
	public Integer selectRowCount(Map<String, Object> map) {
		
		return qnaMapper.selectRowCount(map);
	}

	@Override
	public void insertQna(QnaVO qna) {
		qnaMapper.insertQna(qna);
		
	}

	@Override
	public QnaVO selectQna(Long qna_num) {
		
		return qnaMapper.selectQna(qna_num);
	}

	@Override
	public void updateQna(QnaVO qna) {
		qnaMapper.updateQna(qna);
		
	}

	@Override
	public void deleteQna(Long qna_num) {
		qnaMapper.deleteQna(qna_num);
		
	}

	@Override
	public void deletePhoto(Long qna_num) {
		qnaMapper.deletePhoto(qna_num);
		
	}

	@Override
	public void updateAnswer(QnaVO qna) {
		qnaMapper.updateAnswer(qna);
		
	}

	@Override
	public List<QnaVO> selectListAll(Map<String, Object> map) {
		return qnaMapper.selectListAll(map);
	}

	@Override
	public int selectRowCountAll() {
		return qnaMapper.selectRowCountAll();
	}

	
}