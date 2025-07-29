package kr.spring.policy.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.policy.dao.PolicyMapper;
import kr.spring.policy.vo.PolicyVO;

@Service
@Transactional
public class PolicyServiceImpl implements PolicyService{
	
	@Autowired
	private PolicyMapper policyMapper;

	@Override
	public List<PolicyVO> selectList(Map<String, Object> map) {
		return policyMapper.selectList(map);
	}

	@Override
	public Integer selectRowCount(Map<String, Object> map) {
		return policyMapper.selectRowCount(map);
	}

	@Override
	public void insertPolicy(PolicyVO policy) {
		policyMapper.insertPolicy(policy);
	}

	@Override
	public PolicyVO selectPolicy(Long policy_num) {
		return policyMapper.selectPolicy(policy_num);
	}

	@Override
	public void updatePolicy(PolicyVO policy) {
		policyMapper.updatePolicy(policy);
	}

	@Override
	public void deletePolicy(Long policy_num) {
		policyMapper.deletePolicy(policy_num);
	}


}

	





