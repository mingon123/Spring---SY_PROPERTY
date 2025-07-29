package kr.spring.report.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import kr.spring.report.vo.ReportVO;
import kr.spring.report.vo.Report_typeVO;

public interface ReportService {
	
	// 사용자목록
	public List<ReportVO> selectList(Map<String,Object> map);
	// 게시글 수
	public Integer selectRowCount(Map<String,Object> map);
	// 등록
	public void insertReport(ReportVO report);
	// 상세
	public ReportVO selectReport(@Param("report_num") Long report_num);
	// 수정
	public void updateReport(ReportVO report);
	// 삭제
	public void deleteReport(Long report_num);
	// 사진삭제
	public void deletePhoto(Long report_num);
	
	
	//신고 분류 전체목록
	public List<Report_typeVO> selectTypeList(Map<String,Object> map);
	
	public abstract Long selectUserNumById(String userId);	
	
	// 관리자 전체 목록 조회
	public List<ReportVO> selectListAll(Map<String,Object> map);
	// 관리자 전체 글 수 조회
	public int selectRowCountAll();
}
