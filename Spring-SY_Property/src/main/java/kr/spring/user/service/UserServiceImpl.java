package kr.spring.user.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.user.dao.RealtorDetailMapper;
import kr.spring.user.dao.UserDetailMapper;
import kr.spring.user.dao.UserMapper;
import kr.spring.user.vo.RealtorDetailVO;
import kr.spring.user.vo.UserDetailVO;
import kr.spring.user.vo.UserVO;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserDetailMapper userDetailMapper;
	@Autowired
	private RealtorDetailMapper realtorDetailMapper;
	
	@Override
	public Long selectUserNum() {
		return userMapper.selectUserNum();
	}
	@Override
	public void insertUser(UserVO user) {
		user.setUser_num(userMapper.selectUserNum());
		userMapper.insertUser(user);		
	}
	@Override
	public void insertRealtor(UserVO user) {
		user.setUser_num(userMapper.selectUserNum());
		userMapper.insertRealtor(user);
	}
	@Override
	public UserVO selectUser(Long user_num) {
		return userMapper.selectUser(user_num);
	}
	@Override
	public UserVO selectRealtor(Long user_num) {
		return userMapper.selectRealtor(user_num);
	}
	@Override
	public void insertSocialUser(UserVO user) {
		user.setUser_num(userMapper.selectUserNum());
		userMapper.insertSocialUser(user);
	}
	@Override
	public UserVO selectCheckUser(String id) {
		return userMapper.selectCheckUser(id);
	}
	@Override
	public UserVO selectCheckId(String id) {
		return userMapper.selecChecktId(id);
	}
	@Override
	public UserVO selectCheckNickName(String nick_name) {
		return userMapper.selectCheckNickName(nick_name);
	}
	@Override
	public void updateUser(UserVO user) {
		userMapper.updateUser(user);
	}
	@Override
	public void deleteUser(Long user_num) {
		userMapper.deleteUser(user_num);
		userDetailMapper.deleteUserDetail(user_num);
		realtorDetailMapper.deleteRealtorDetail(user_num);
	}
	@Override
	public void updateUserPassword(UserVO user) {
		userMapper.updateUserPassword(user);
	}
	@Override
	public void updateRandomPassword(UserVO user) {
		userMapper.updateRandomPassword(user);
	}
	@Override
	public UserVO selectIdEmailCheckUser(String id) {
		return userMapper.selectIdEmailCheckUser(id);
	}
	@Override
	public UserVO selectUserByEmail(String email) {
		return userMapper.selectUserByEmail(email);
	}
	@Override
	public UserVO selectRealtorByEmail(String email) {
		return userMapper.selectRealtorByEmail(email);
	}
	@Override
	public void insertUserDetail(UserDetailVO user) {
		userDetailMapper.insertUserDetail(user);
	}
	@Override
	public void updateUserDetail(UserDetailVO user) {
		userDetailMapper.updateUserDetail(user);
	}
	@Override
	public void updateUserProfile(UserDetailVO user) {
	    userDetailMapper.updateProfile(user);
	}
	@Override
	public void insertRealtorDetail(RealtorDetailVO realtor) {
		realtorDetailMapper.insertRealtorDetail(realtor);
	}
	@Override
	public void updateRealtorDetail(RealtorDetailVO realtor) {
		realtorDetailMapper.updateRealtorDetail(realtor);
	}
	@Override
	public void updateRealtorProfile(RealtorDetailVO realtor) {
	    realtorDetailMapper.updateRealtorProfile(realtor);
	}
	@Override
	public void updateCertificate(RealtorDetailVO realtor) {
		realtorDetailMapper.updateCertificate(realtor);
	}
	@Override
	public void updateValidDate(Long realtor_num) {
		realtorDetailMapper.updateValidDate(realtor_num);
	}
	@Override
	public void updateNoneValidDate(Long realtor_num) {
		realtorDetailMapper.updateNoneValidDate(realtor_num);
	}
	@Override
	public Integer selectRowCount(Map<String, Object> map) {
		return userMapper.selectRowCount(map);
	}
	@Override
	public List<UserVO> selectList(Map<String, Object> map) {
		return userMapper.selectList(map);
	}
	@Override
	public Integer selectRealtorRowCount(Map<String, Object> map) {
		return userMapper.selectRealtorRowCount(map);
	}
	@Override
	public List<UserVO> selectRealtorList(Map<String, Object> map){
		return userMapper.selectRealtorList(map);
	}
	@Override
	public void updateByAdmin(UserVO user) {
		userMapper.updateByAdmin(user);
	}
	@Override
	public void updateAuthorityToRealtor(Long user_num) {
		userMapper.updateAuthorityToRealtor(user_num);
	}
	@Override
	public void saveOrUpdateRealtorDetail(UserVO userVO) {
	    RealtorDetailVO detail = userVO.getRealtorDetail();
	    Long userNum = userVO.getUser_num();
	    // realtor_detail에 user_num이 있는지 확인
	    long num = realtorDetailMapper.countByUserNum(userNum);
	    if(num == 0){
	        realtorDetailMapper.insertRealtorDetail(detail);
	    }else{
	        realtorDetailMapper.updateRealtorDetail(detail);
	        // 파일(자격증) 첨부가 있을 때만
	        if(detail.getCertificate() != null && detail.getCertificate_name() != null){
	            realtorDetailMapper.updateCertificate(detail);
	        }
	    }
	}
	@Override
	public void updateReportCount(Long user_num, int report_count) {
		if (report_count < 0) report_count = 0;
		if (report_count > 3) report_count = 3;

		userMapper.updateReportCount(user_num, report_count);
	}
	@Override
	public int getReportCount(Long user_num) {
		return userMapper.getReportCount(user_num);
	}

	

	
	@Override
	public UserVO selectUserByNum(Long user_num) {
	    return userMapper.selectUser(user_num); 
	}

}
