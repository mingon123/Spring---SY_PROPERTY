package kr.spring.property.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.spring.property.service.PropertyService;
import kr.spring.property.vo.PropertyFavVO;
import kr.spring.property.vo.PropertyVO;
import kr.spring.user.vo.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/property")
public class PropertyRestController {

	@Autowired
	private PropertyService propertyService;

	//자바빈(VO) 초기화
	@ModelAttribute
	public PropertyVO initCommand() {
		return new PropertyVO();
	}

	//전체매물 목록
	@GetMapping("/list")
	public List<PropertyVO>getPropertyList(){
        log.debug("<<전체 매물 리스트 요청>>");
		return propertyService.getPropertiesList();
	}
	
	//특정 카테고리 매물 목록
	@GetMapping("/list/{category}")
	public List<PropertyVO> getPropertiesListByCategory(
											@PathVariable("category") String category,
											@AuthenticationPrincipal PrincipalDetails principal){
		log.debug("<<매물 리스트 요청>> category : {}",category);
		Map<String, Object> map = new HashMap<>();
		map.put("category", category);
		if (principal != null) {
			map.put("user_num", principal.getUserVO().getUser_num());
		}
		return propertyService.getPropertiesListByCategory(map);

	}
	
	//매물 관심 확인 - 단일 매물(매물리스트에는 사용 x)
	@GetMapping("/getFav/{property_id}")
	public ResponseEntity<Map<String,Object>> getFav(
				           @PathVariable("property_id") long property_id,
				           @AuthenticationPrincipal PrincipalDetails principal){
			
			log.debug("<<매물 좋아요 확인>> property_id : {}",property_id);
			
			Map<String,Object> mapAjax = new HashMap<String,Object>();
			PropertyFavVO fav = new PropertyFavVO();
			fav.setProperty_id(property_id);
			if(principal == null) {
				mapAjax.put("status", "noFav");
			}else {
				//로그인된 회원번호 셋팅
				fav.setUser_num(principal.getUserVO().getUser_num());
				
				PropertyFavVO propertyFav = propertyService.selectFav(fav);
				
				if(propertyFav!=null) {
					mapAjax.put("status", "yesFav");
				}else {
					mapAjax.put("status", "noFav");
				}
			}
			
			return new ResponseEntity<Map<String,Object>>(mapAjax,HttpStatus.OK);
		}
		
		//매물 좋아요 등록/삭제
		@PostMapping("/writeFav")
		public ResponseEntity<Map<String,Object>> writeFav(
				             @RequestBody PropertyFavVO fav,
				             @AuthenticationPrincipal PrincipalDetails principal){
			log.debug("<<매물 좋아요 등록/삭제> : {}",fav);
			
			Map<String,Object> mapAjax = new HashMap<String,Object>();
			if(principal==null) {
				mapAjax.put("result", "logout");
			}else {
				//로그인된 회원번호 셋팅
				fav.setUser_num(principal.getUserVO().getUser_num());
				PropertyFavVO propertyFav = propertyService.selectFav(fav);
				if(propertyFav!=null) {
					propertyService.deleteFav(fav);
					mapAjax.put("status", "noFav");
				}else {
					propertyService.insertFav(fav);
					mapAjax.put("status", "yesFav");
				}
				mapAjax.put("result", "success");
			}		
			return new ResponseEntity<Map<String,Object>>(mapAjax,HttpStatus.OK);
		}
		
		// 로그인 사용자가 좋아요한 매물 ID 리스트 반환하는 함수 추가
		@GetMapping("/favList")
		public ResponseEntity<List<Long>> getFavPropertyId(@AuthenticationPrincipal PrincipalDetails principal) {
		    if (principal == null) {
		        return ResponseEntity.ok().body(List.of()); // 비로그인 사용자는 빈 리스트 반환
		    }
		    Long userNum = principal.getUserVO().getUser_num();
		    List<Long> FavList = propertyService.getFavPropertyId(userNum);
		    return ResponseEntity.ok().body(FavList);
		}


	

}
