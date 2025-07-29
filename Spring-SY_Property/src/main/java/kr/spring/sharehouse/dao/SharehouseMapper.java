package kr.spring.sharehouse.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.sharehouse.vo.SharehouseImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomVO;
import kr.spring.sharehouse.vo.SharehouseSchoolsVO;
import kr.spring.sharehouse.vo.SharehouseSubwaysVO;
import kr.spring.sharehouse.vo.SharehouseVO;

@Mapper
public interface SharehouseMapper {
	//xml에서 작업
	public List<SharehouseVO> selectList(Map<String, Object> map);
	//xml에서 작업
	public Integer selectRowCount(Map<String, Object> map);
	
	@Select("SELECT * FROM sharehouse_img "
			+ "WHERE house_id=#{house_id} AND sh_img_type='HOUSE' AND sh_img_seq=1")	
	public SharehouseImgVO selectSharehouseTitleImage(Long house_id);	
	@Select("SELECT * FROM sharehouse_img WHERE house_id=#{house_id} AND sh_img_type=#{sh_img_type} ORDER BY sh_img_seq")
	public List<SharehouseImgVO> selectSharehouseImages(Long house_id, String sh_img_type);
	@Select("SELECT * FROM sharehouse_img WHERE house_id=#{house_id} AND sh_img_type=#{sh_img_type} AND sh_img_seq=#{sh_img_seq}")
	public SharehouseImgVO selectSharehouseImage(Long house_id, String sh_img_type, int sh_img_seq);
	
	@Select("SELECT * FROM sharehouse_room_img WHERE room_id=#{room_id} AND shr_img_seq=1")
	public SharehouseRoomImgVO selectSharehouseRoomTitleImage(Long room_id);
	@Select("SELECT sri.* FROM sharehouse_room_img sri JOIN sharehouse_room sr ON sri.room_id = sr.room_id "
			+ "WHERE sr.house_id = #{house_id} ORDER BY sri.room_id, sri.shr_img_seq")
	public List<SharehouseRoomImgVO> selectSharehouseRoomImages(Long house_id);
	@Select("SELECT * FROM sharehouse_room_img WHERE room_id=#{room_id} AND shr_img_seq=#{shr_img_seq}")
	public SharehouseRoomImgVO selectSharehouseRoomImage(Long room_id, int shr_img_seq);

	//xml에서 작업
	public SharehouseVO selectSharehouse(Long house_id);
	@Update("UPDATE sharehouse SET s_hit=s_hit+1 WHERE house_id=#{house_id}")
	public void updateHit(Long house_id);
	//xml에서 작업
	public List<SharehouseRoomVO> selectSharehouseRooms(Long house_id);
	
	
	// 관리자
	//xml에서 작업
    public void updateSharehouse(SharehouseVO sharehouseVO);
    @Delete("DELETE FROM sharehouse WHERE house_id=#{house_id}")
    public void deleteSharehouse(Long house_id);
    
    @Delete("DELETE FROM sharehouse_img WHERE house_id=#{house_id} AND sh_img_type=#{sh_img_type} AND sh_img_seq=#{sh_img_seq}")
    public void deleteSharehouseImg(Long house_id, String sh_img_type, int sh_img_seq);
    @Delete("DELETE FROM sharehouse_img WHERE house_id=#{house_id}")
    public void deleteAllSharehouseImgs(Long house_id);

    public void insertSharehouseRoom(SharehouseRoomVO roomVO);    
    public void updateSharehouseRoom(SharehouseRoomVO roomVO);
    @Delete("DELETE FROM sharehouse_room WHERE room_id=#{room_id}")
    public void deleteSharehouseRoom(Long room_id);
    @Delete("DELETE FROM sharehouse_room WHERE house_id=#{house_id}")
    public void deleteAllSharehouseRooms(Long house_id);
    
    public void insertSharehouseRoomImg(SharehouseRoomImgVO imgVO);
    public void deleteSharehouseRoomImg(Long room_id, int shr_img_seq);
    
    @Select("SELECT * FROM sharehouse_subways WHERE house_id=#{house_id}")
    public List<SharehouseSubwaysVO> selectSharehouseSubways(Long house_id);
    @Select("SELECT * FROM sharehouse_schools WHERE house_id=#{house_id}")
    public List<SharehouseSchoolsVO> selectSharehouseSchools(Long house_id);
    @Select("SELECT * FROM sharehouse_room_img WHERE room_id=#{room_id} ORDER BY shr_img_seq")
    public List<SharehouseRoomImgVO> selectSharehouseRoomImagesByRoomId(Long room_id);
    
    public void insertSharehouseSubway(SharehouseSubwaysVO vo);
    public void deleteSharehouseSubways(Long house_id);

    public void insertSharehouseSchool(SharehouseSchoolsVO vo);
    public void deleteSharehouseSchools(Long house_id);
    public void insertSharehouseImg(SharehouseImgVO imgVO);
    
} //class
