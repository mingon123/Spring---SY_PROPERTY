package kr.spring.sharehouse.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.sharehouse.dao.SharehouseMapper;
import kr.spring.sharehouse.vo.SharehouseImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomVO;
import kr.spring.sharehouse.vo.SharehouseSchoolsVO;
import kr.spring.sharehouse.vo.SharehouseSubwaysVO;
import kr.spring.sharehouse.vo.SharehouseVO;

@Service
@Transactional
public class SharehouseServiceImpl implements SharehouseService{
	
	@Autowired
	private SharehouseMapper sharehouseMapper;

	@Override
	public List<SharehouseVO> selectList(Map<String, Object> map) {
		return sharehouseMapper.selectList(map);
	}

	@Override
	public Integer selectRowCount(Map<String, Object> map) {
		return sharehouseMapper.selectRowCount(map);
	}


	@Override
	public SharehouseImgVO selectSharehouseTitleImage(Long house_id) {
		return sharehouseMapper.selectSharehouseTitleImage(house_id);
	}
	
	@Override
	public List<SharehouseImgVO> selectSharehouseImages(Long house_id, String sh_img_type) {
		return sharehouseMapper.selectSharehouseImages(house_id, sh_img_type);
	}
	
	@Override
	public SharehouseImgVO selectSharehouseImage(Long house_id, String sh_img_type, int sh_img_seq) {
		return sharehouseMapper.selectSharehouseImage(house_id, sh_img_type, sh_img_seq);
	}	

	@Override
	public SharehouseRoomImgVO selectSharehouseRoomTitleImage(Long room_id) {
		return sharehouseMapper.selectSharehouseRoomTitleImage(room_id);
	}
	
	@Override
	public List<SharehouseRoomImgVO> selectSharehouseRoomImages(Long room_id) {
		return sharehouseMapper.selectSharehouseRoomImages(room_id);
	}

	@Override
	public SharehouseRoomImgVO selectSharehouseRoomImage(Long room_id, int shr_img_seq) {
		return sharehouseMapper.selectSharehouseRoomImage(room_id, shr_img_seq);
	}
	
	@Override
	public SharehouseVO selectSharehouse(Long house_id) {
		return sharehouseMapper.selectSharehouse(house_id);
	}
	
	@Override
	public void updateHit(Long house_id) {
		sharehouseMapper.updateHit(house_id);
	}

	@Override
	public List<SharehouseRoomVO> selectSharehouseRooms(Long house_id) {
		return sharehouseMapper.selectSharehouseRooms(house_id);
	}

	@Override
	public void updateSharehouse(SharehouseVO sharehouseVO) {
		sharehouseMapper.updateSharehouse(sharehouseVO);
	}

	@Override
	public void deleteSharehouse(Long house_id) {
		sharehouseMapper.deleteSharehouse(house_id);
	}

	@Override
	public void deleteSharehouseImg(Long house_id, String sh_img_type, int sh_img_seq) {
		sharehouseMapper.deleteSharehouseImg(house_id, sh_img_type, sh_img_seq);
	}

	@Override
	public void deleteAllSharehouseImgs(Long house_id) {
		sharehouseMapper.deleteAllSharehouseImgs(house_id);
	}

	@Override
	public void insertSharehouseRoom(SharehouseRoomVO roomVO) {
		sharehouseMapper.insertSharehouseRoom(roomVO);
	}

	@Override
	public void updateSharehouseRoom(SharehouseRoomVO roomVO) {
		sharehouseMapper.updateSharehouseRoom(roomVO);
	}

	@Override
	public void deleteSharehouseRoom(Long room_id) {
		sharehouseMapper.deleteSharehouseRoom(room_id);
	}

	@Override
	public void deleteAllSharehouseRooms(Long house_id) {
		sharehouseMapper.deleteAllSharehouseRooms(house_id);
	}

	@Override
	public void insertSharehouseRoomImg(SharehouseRoomImgVO imgVO) {
		sharehouseMapper.insertSharehouseRoomImg(imgVO);
	}

	@Override
	public void deleteSharehouseRoomImg(Long room_id, int shr_img_seq) {
		sharehouseMapper.deleteSharehouseRoomImg(room_id, shr_img_seq);
	}

	@Override
	public List<SharehouseSubwaysVO> selectSharehouseSubways(Long house_id) {
		return sharehouseMapper.selectSharehouseSubways(house_id);
	}

	@Override
	public List<SharehouseSchoolsVO> selectSharehouseSchools(Long house_id) {
		return sharehouseMapper.selectSharehouseSchools(house_id);
	}

	@Override
	public List<SharehouseRoomImgVO> selectSharehouseRoomImagesByRoomId(Long room_id) {
		return sharehouseMapper.selectSharehouseRoomImagesByRoomId(room_id);
	}

	@Override
	public void insertSharehouseSubway(SharehouseSubwaysVO vo) {
		sharehouseMapper.insertSharehouseSubway(vo);
	}

	@Override
	public void deleteSharehouseSubways(Long house_id) {
		sharehouseMapper.deleteSharehouseSubways(house_id);
	}

	@Override
	public void insertSharehouseSchool(SharehouseSchoolsVO vo) {
		sharehouseMapper.insertSharehouseSchool(vo);
	}

	@Override
	public void deleteSharehouseSchools(Long house_id) {
		sharehouseMapper.deleteSharehouseSchools(house_id);
	}

	@Override
	public void insertSharehouseImg(SharehouseImgVO imgVO) {
		sharehouseMapper.insertSharehouseImg(imgVO);
	}

	@Override
	public void updateSharehouseAll(SharehouseVO sharehouseVO, List<SharehouseRoomVO> rooms,
			List<SharehouseSubwaysVO> subways, List<SharehouseSchoolsVO> schools) {
	    // 쉐어하우스 본체 수정
	    sharehouseMapper.updateSharehouse(sharehouseVO);

	    // 기존 Room, Subway, School 전체 삭제 후 새로 등록
	    if (rooms != null) {
	        sharehouseMapper.deleteAllSharehouseRooms(sharehouseVO.getHouse_id());
	        for (SharehouseRoomVO room : rooms) {
	            room.setHouse_id(sharehouseVO.getHouse_id());
	            sharehouseMapper.insertSharehouseRoom(room);
	        }
	    }
	    if (subways != null) {
	        sharehouseMapper.deleteSharehouseSubways(sharehouseVO.getHouse_id());
	        for (SharehouseSubwaysVO subway : subways) {
	            subway.setHouse_id(sharehouseVO.getHouse_id());
	            sharehouseMapper.insertSharehouseSubway(subway);
	        }
	    }
	    if (schools != null) {
	        sharehouseMapper.deleteSharehouseSchools(sharehouseVO.getHouse_id());
	        for (SharehouseSchoolsVO school : schools) {
	            school.setHouse_id(sharehouseVO.getHouse_id());
	            sharehouseMapper.insertSharehouseSchool(school);
	        }
	    }
	}




	
} //class
