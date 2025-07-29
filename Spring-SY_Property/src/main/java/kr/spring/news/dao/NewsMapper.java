package kr.spring.news.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.news.vo.NewsVO;

@Mapper
public interface NewsMapper {

	public List<NewsVO> selectList(Map<String,Object> map);
	public Integer selectRowCount(Map<String,Object> map);
	public void insertNews(NewsVO news);
	public NewsVO selectNews(Long news_num);
	public void updateNews(NewsVO news);
	public void deleteNews(Long news_num);
	public void deletePhoto(Long news_num);

}









