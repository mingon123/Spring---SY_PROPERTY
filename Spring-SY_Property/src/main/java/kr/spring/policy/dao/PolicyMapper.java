package kr.spring.policy.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import kr.spring.notice.vo.NoticeVO;
import kr.spring.policy.vo.PolicyVO;

@Mapper
public interface PolicyMapper {

	public List<PolicyVO> selectList(Map<String,Object> map);
	public Integer selectRowCount(Map<String,Object> map);
	public void insertPolicy(PolicyVO policy);
	public PolicyVO selectPolicy(Long policy_num);
	public void updatePolicy(PolicyVO policy);
	public void deletePolicy(Long policy_num);

}









