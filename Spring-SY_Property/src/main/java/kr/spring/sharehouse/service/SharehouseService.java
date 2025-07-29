package kr.spring.sharehouse.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import kr.spring.sharehouse.vo.SharehouseImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomVO;
import kr.spring.sharehouse.vo.SharehouseSchoolsVO;
import kr.spring.sharehouse.vo.SharehouseSubwaysVO;
import kr.spring.sharehouse.vo.SharehouseVO;

public interface SharehouseService {
	
	public List<SharehouseVO> selectList(Map<String, Object> map);
	public Integer selectRowCount(Map<String, Object> map);
	
	public SharehouseImgVO selectSharehouseTitleImage(Long house_id);
	public List<SharehouseImgVO> selectSharehouseImages(Long house_id, String sh_img_type);
	public SharehouseImgVO selectSharehouseImage(Long house_id, String sh_img_type, int sh_img_seq);
	
	public SharehouseRoomImgVO selectSharehouseRoomTitleImage(Long room_id);
	public List<SharehouseRoomImgVO> selectSharehouseRoomImages(Long room_id);
	public SharehouseRoomImgVO selectSharehouseRoomImage(Long room_id, int shr_img_seq); 
	
	public SharehouseVO selectSharehouse(Long house_id);
	public void updateHit(Long house_id);
	public List<SharehouseRoomVO> selectSharehouseRooms(Long house_id);
	
	// 관리자
    public void updateSharehouse(SharehouseVO sharehouseVO);
    public void deleteSharehouse(Long house_id);
    public void deleteSharehouseImg(Long house_id, String sh_img_type, int sh_img_seq);
    public void deleteAllSharehouseImgs(Long house_id);

    public void insertSharehouseRoom(SharehouseRoomVO roomVO);    
    public void updateSharehouseRoom(SharehouseRoomVO roomVO);
    public void deleteSharehouseRoom(Long room_id);
    public void deleteAllSharehouseRooms(Long house_id);
    
    public void insertSharehouseRoomImg(SharehouseRoomImgVO imgVO);
    public void deleteSharehouseRoomImg(Long room_id, int shr_img_seq);
    
    public List<SharehouseSubwaysVO> selectSharehouseSubways(Long house_id);
    public List<SharehouseSchoolsVO> selectSharehouseSchools(Long house_id);
    public List<SharehouseRoomImgVO> selectSharehouseRoomImagesByRoomId(Long room_id);
    
    public void insertSharehouseSubway(SharehouseSubwaysVO vo);
    public void deleteSharehouseSubways(Long house_id);

    public void insertSharehouseSchool(SharehouseSchoolsVO vo);
    public void deleteSharehouseSchools(Long house_id);
    public void insertSharehouseImg(SharehouseImgVO imgVO);
    // 관리자 - 수정페이지
    public void updateSharehouseAll(SharehouseVO sharehouseVO,List<SharehouseRoomVO> rooms,List<SharehouseSubwaysVO> subways,List<SharehouseSchoolsVO> schools);
    
} //interface
