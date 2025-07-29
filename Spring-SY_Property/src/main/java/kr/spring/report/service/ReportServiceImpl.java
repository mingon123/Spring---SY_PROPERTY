package kr.spring.report.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.report.dao.ReportMapper;
import kr.spring.report.vo.ReportVO;
import kr.spring.report.vo.Report_typeVO;


@Service
@Transactional
public class ReportServiceImpl implements ReportService{
	
	@Autowired
	private ReportMapper reportMapper;

	@Override
	public List<ReportVO> selectList(Map<String, Object> map) {
		
		return reportMapper.selectList(map);
	}

	@Override
	public Integer selectRowCount(Map<String, Object> map) {
		
		return reportMapper.selectRowCount(map);
	}

	@Override
	public void insertReport(ReportVO report) {
		reportMapper.insertReport(report);
		
	}

	@Override
	public ReportVO selectReport(Long report_num) {
		
		return reportMapper.selectReport(report_num);
	}

	@Override
	public void updateReport(ReportVO report) {
		reportMapper.updateReport(report);
		
	}

	@Override
	public void deleteReport(Long report_num) {
		reportMapper.deleteReport(report_num);
		
	}

	@Override
	public void deletePhoto(Long report_num) {
		reportMapper.deletePhoto(report_num);
		
	}

	@Override
	public List<ReportVO> selectListAll(Map<String, Object> map) {
		
		return reportMapper.selectListAll(map);
	}

	@Override
	public int selectRowCountAll() {
		
		return reportMapper.selectRowCountAll();
	}

	@Override
	public List<Report_typeVO> selectTypeList(Map<String, Object> map) {
		
		return reportMapper.selectTypeList(map);
	}

	@Override
	public Long selectUserNumById(String userId) {
		return reportMapper.selectUserNumById(userId);
	}

}






















