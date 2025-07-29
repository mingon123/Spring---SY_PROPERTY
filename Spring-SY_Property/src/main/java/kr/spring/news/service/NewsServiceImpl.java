package kr.spring.news.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.news.dao.NewsMapper;
import kr.spring.news.vo.NewsVO;


@Service
@Transactional
public class NewsServiceImpl implements NewsService{
	
	@Autowired
	private NewsMapper newsMapper;

	@Override
	public List<NewsVO> selectList(Map<String, Object> map) {
		
		return newsMapper.selectList(map);
	}

	@Override
	public Integer selectRowCount(Map<String, Object> map) {
		
		return newsMapper.selectRowCount(map);
	}

	@Override
	public void insertNews(NewsVO news) {
		newsMapper.insertNews(news);
		
	}

	@Override
	public NewsVO selectNews(Long news_num) {
		
		return newsMapper.selectNews(news_num);
	}

	@Override
	public void updateNews(NewsVO news) {
		newsMapper.updateNews(news);
		
	}

	@Override
	public void deleteNews(Long news_num) {
		newsMapper.deleteNews(news_num);
		
	}

	@Override
	public void deletePhoto(Long news_num) {
		newsMapper.deletePhoto(news_num);
		
	}

	

	
}

	





