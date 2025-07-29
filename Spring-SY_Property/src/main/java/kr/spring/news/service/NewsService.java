package kr.spring.news.service;

import java.util.List;
import java.util.Map;

import kr.spring.news.vo.NewsVO;

public interface NewsService {
	
	//목록
	public List<NewsVO> selectList(Map<String,Object> map);
	//게시글 수
	public Integer selectRowCount(Map<String,Object> map);
	//등록
	public void insertNews(NewsVO news);
	//상세
	public NewsVO selectNews(Long news_num);
	//수정
	public void updateNews(NewsVO news);
	//삭제
	public void deleteNews(Long news_num);
	//사진삭제
	public void deletePhoto(Long news_num);
}





