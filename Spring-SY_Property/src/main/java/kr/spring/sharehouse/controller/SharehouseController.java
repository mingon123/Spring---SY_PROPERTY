package kr.spring.sharehouse.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kr.spring.sharehouse.service.SharehouseService;
import kr.spring.sharehouse.vo.SharehouseImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomVO;
import kr.spring.sharehouse.vo.SharehouseVO;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/sharehouse")
public class SharehouseController {

	@Autowired
	private SharehouseService sharehouseService;
	
	//쉐어하우스 목록
	@GetMapping("/list")
	public String sharehouseList(
			@RequestParam(defaultValue="1") int pageNum,
			@RequestParam(defaultValue="1") int order,
			String sido, String sigungu,
			String keyfield, String keyword, Model model) {
		
		//log.debug("<<쉐어하우스 목록>> {}", );
		
		// 시도 전체 선택 상태인데 시군구만 선택된 경우 sigungu 초기화
		if ((sido == null || sido.isBlank()) && sigungu != null && !sigungu.isBlank()) {
			sigungu = null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();		
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		map.put("sido",sido);
		map.put("sigungu",sigungu);
		
		//전체,검색 레코드 수
		int count = sharehouseService.selectRowCount(map);
		
		//페이지 처리 (Q.addKey 파라미터 꼭 있어야 하는지?)
		PagingUtil page = new PagingUtil(keyfield, keyword, pageNum, count, 10, 10, "list");
		
		List<SharehouseVO> list = null;
		if (count > 0) {
			map.put("order", order);
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			
			list = sharehouseService.selectList(map);
		} // if
		
	    //시군구 리스트 모델에 추가
	    if (sido != null && !sido.isBlank()) {
	        List<String> sigunguList = switch (sido) {
	            case "서울" -> List.of("강남구","강동구","강북구","강서구","관악구","광진구",
	            					  "구로구","노원구","도봉구","동대문구","동작구","마포구",
	            					  "서대문구","서초구","성동구","성북구","송파구","양천구",
	            					  "영등포구","용산구","은평구","종로구","중구","중랑구");
	            //TODO: 추후 나머지 지역 추가 요망
	            case "경기" -> List.of("고양시 일산동구(장항동)", "고양시 일산서구(일산동)", "구리시(갈매동)");
	            default -> List.of();
	        };
	        model.addAttribute("sigunguList", sigunguList);
	    }
		
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		model.addAttribute("order", order);
		model.addAttribute("keyfield", keyfield);
		model.addAttribute("keyword", keyword);	
		model.addAttribute("sido",sido);
		model.addAttribute("sigungu",sigungu);
		
		return "views/sharehouse/sharehouseList";
	}
	
	//쉐어하우스 상세
	@GetMapping("/detail")
	public String getDetail(long house_id, Model model) {
		
		log.debug("<<쉐어하우스 상세>> house_id : {}", house_id);
		
		//해당 쉐어하우스의 조회수 증가
		sharehouseService.updateHit(house_id);	
			
		SharehouseVO sharehouse = sharehouseService.selectSharehouse(house_id);
		List<SharehouseImgVO> houseImages = sharehouseService.selectSharehouseImages(house_id, "HOUSE");
		List<SharehouseImgVO> structureImages = sharehouseService.selectSharehouseImages(house_id, "STRUCTURE");
		List<SharehouseRoomVO> roomList = sharehouseService.selectSharehouseRooms(house_id);
		
		model.addAttribute("sharehouse", sharehouse);
		model.addAttribute("houseImages", houseImages);
		model.addAttribute("structureImages", structureImages);
		model.addAttribute("roomList", roomList);
		
		return "views/sharehouse/sharehouseView";
	} //getDetail
	
	//쉐어하우스 방 상세
	@GetMapping("/room_detail")
	public String getRoomDetail(long house_id, Model model) {
		
		log.debug("<<쉐어하우스 방 상세>> house_id : {}", house_id);
		
		List<SharehouseRoomVO> roomList = sharehouseService.selectSharehouseRooms(house_id);
		List<SharehouseRoomImgVO> roomImages = sharehouseService.selectSharehouseRoomImages(house_id);
		
		model.addAttribute("roomList", roomList);
		model.addAttribute("roomImages", roomImages);
		
		return "views/sharehouse/sharehouseRoomView";
	} //getRoomDetail
		
	//쉐어하우스 대표 이미지 출력
	@GetMapping("/viewSharehouseTitleImage")
	public String getSharehouseTitleImage(long house_id, 
			HttpServletRequest request, Model model) {
		SharehouseImgVO sharehouseImgVO = sharehouseService.selectSharehouseTitleImage(house_id);
		viewSharehouseImage(sharehouseImgVO, request, model);
		
		return "imageView";		
	}	
	
	//쉐어하우스 방 대표 이미지 출력
	@GetMapping("/viewSharehouseRoomTitleImage")
	public String getSharehouseRoomTitleImage(long room_id, 
										HttpServletRequest request, Model model) {
		SharehouseRoomImgVO sharehouseRoomImgVO = sharehouseService.selectSharehouseRoomTitleImage(room_id);
		viewSharehouseRoomImage(sharehouseRoomImgVO, request, model);
		
		return "imageView";		
	}	
	
	//쉐어하우스 전체 이미지 출력
	@GetMapping("/viewSharehouseImage")
	public String getSharehouseImage(long house_id, String sh_img_type, int sh_img_seq,
			HttpServletRequest request,
			Model model) {
		SharehouseImgVO img = sharehouseService.selectSharehouseImage(house_id,sh_img_type,sh_img_seq);
		viewSharehouseImage(img, request, model); 
		
		return "imageView";
	}
	
	//쉐어하우스 방 전체 이미지 출력
	@GetMapping("/viewSharehouseRoomImage")
	public String getSharehouseRoomImage(long room_id, int shr_img_seq,
										  HttpServletRequest request,
										  Model model) {
		SharehouseRoomImgVO img = sharehouseService.selectSharehouseRoomImage(room_id,shr_img_seq);
		viewSharehouseRoomImage(img, request, model);
		
		return "imageView";
	}
	
	//쉐어하우스 이미지 처리를 위한 코드
	public void viewSharehouseImage(SharehouseImgVO sharehouseImgVO, 
			HttpServletRequest request, Model model) {
		if (sharehouseImgVO == null || sharehouseImgVO.getSh_img_name()==null) {
			//DB에 저장된 쉐어하우스 이미지가 없기 때문에 기본 이미지 호출
			getBasicSharehouseImage(request, model);
		} else {
			model.addAttribute("imageFile", sharehouseImgVO.getSh_img());
			model.addAttribute("filename", sharehouseImgVO.getSh_img_name());
		}// if
	}
	
	//쉐어하우스 방 이미지 처리를 위한 코드
	public void viewSharehouseRoomImage(SharehouseRoomImgVO sharehouseRoomImgVO, 
							HttpServletRequest request, Model model) {
		if (sharehouseRoomImgVO == null || sharehouseRoomImgVO.getShr_img_name()==null) {
			//DB에 저장된 쉐어하우스 방 이미지가 없기 때문에 기본 이미지 호출
			getBasicSharehouseRoomImage(request, model);
		} else {
			model.addAttribute("imageFile", sharehouseRoomImgVO.getShr_img());
			model.addAttribute("filename", sharehouseRoomImgVO.getShr_img_name());
		}// if
	}
	
	//기본 이미지 읽기(쉐어하우스)
	public void getBasicSharehouseImage(HttpServletRequest request, Model model) {
		byte[] readbyte = 
				FileUtil.getBytes(
						request.getServletContext()
							   .getRealPath("/assets/image_bundle/house-sharing.png"));
		model.addAttribute("imageFile", readbyte);
		model.addAttribute("filename", "house-sharing.png");
	}
	
	//기본 이미지 읽기(방)
	public void getBasicSharehouseRoomImage(HttpServletRequest request, Model model) {
		byte[] readbyte = 
				FileUtil.getBytes(
						request.getServletContext()
							   .getRealPath("/assets/image_bundle/room.png"));
		model.addAttribute("imageFile", readbyte);
		model.addAttribute("filename", "room.png");
	}
	
	
	
	
} //class