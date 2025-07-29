package kr.spring.policy.service;

import java.util.List;
import java.util.Map;

import kr.spring.policy.vo.PolicyVO;

public interface PolicyService {
	
	//목록
	public List<PolicyVO> selectList(Map<String,Object> map);
	//게시글 수
	public Integer selectRowCount(Map<String,Object> map);
	//등록
	public void insertPolicy(PolicyVO policy);
	//상세
	public PolicyVO selectPolicy(Long policy_num);
	//수정
	public void updatePolicy(PolicyVO policy);
	//삭제
	public void deletePolicy(Long policy_num);
}





