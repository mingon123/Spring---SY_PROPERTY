package kr.spring.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.spring.report.vo.ReportVO;
import kr.spring.report.vo.Report_typeVO;

@Mapper
public interface ReportMapper {

	public List<ReportVO> selectList(Map<String,Object> map);
	public Integer selectRowCount(Map<String,Object> map);
	public void insertReport(ReportVO report);
	public ReportVO selectReport(@Param("report_num") Long report_num);
	public void updateReport(ReportVO report);
	public void deleteReport(Long report_num);
	public void deletePhoto(Long report_num);
	
	//신고분류유형 목록
	public List<Report_typeVO> selectTypeList(Map<String, Object> map); 

	// 관리자용 전체 글 수 조회
    public int selectRowCountAll();
    
    public abstract Long selectUserNumById(String userId);

    // 관리자용 전체 목록 조회 (페이징용 파라미터 포함)
    public List<ReportVO> selectListAll(Map<String,Object> map);
}










