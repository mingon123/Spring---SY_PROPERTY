package kr.spring.sharehouse.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import groovy.util.logging.Slf4j;
import jakarta.validation.Valid;
import kr.spring.sharehouse.service.SharehouseService;
import kr.spring.sharehouse.vo.SharehouseImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomImgVO;
import kr.spring.sharehouse.vo.SharehouseRoomVO;
import kr.spring.sharehouse.vo.SharehouseSchoolsVO;
import kr.spring.sharehouse.vo.SharehouseSubwaysVO;
import kr.spring.sharehouse.vo.SharehouseVO;
import kr.spring.util.PagingUtil;
import kr.spring.util.ValidationUtil;

@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/sharehouse")
public class SharehouseAdminController {

    @Autowired
    private SharehouseService sharehouseService;

    // 관리자 목록 페이지
    @GetMapping("/admin_list")
    public String list(
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(name = "sido", required = false) String sido,
            @RequestParam(name = "sigungu", required = false) String sigungu,
            @RequestParam(name = "order", defaultValue = "1") int order,
            Model model) {

        Map<String, Object> map = new HashMap<>();
        map.put("sido", sido);
        map.put("sigungu", sigungu);
        map.put("order", order);

        int count = sharehouseService.selectRowCount(map);

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "admin_list", null);

        List<SharehouseVO> list = null; 
        if (count > 0) {
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());
            list = sharehouseService.selectList(map);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());
        model.addAttribute("sido", sido);
        model.addAttribute("sigungu", sigungu);
        model.addAttribute("order", order);
        
        return "views/sharehouse/admin_list";
    }
    
    // 상세페이지
    @GetMapping("/admin_detail/{house_id}")
    public String adminDetail(@PathVariable Long house_id, Model model) {
        // 쉐어하우스 기본 정보
        SharehouseVO sharehouse = sharehouseService.selectSharehouse(house_id);
        model.addAttribute("sharehouseVO", sharehouse);

        // 쉐어하우스 이미지(HOUSE)
        List<SharehouseImgVO> houseImages = sharehouseService.selectSharehouseImages(house_id, "HOUSE");
        model.addAttribute("houseImages", houseImages);

        // 쉐어하우스 구조 이미지(STRUCTURE)
        List<SharehouseImgVO> structureImages = sharehouseService.selectSharehouseImages(house_id, "STRUCTURE");
        model.addAttribute("structureImages", structureImages);

        // 룸 리스트
        List<SharehouseRoomVO> rooms = sharehouseService.selectSharehouseRooms(house_id);
        model.addAttribute("rooms", rooms);

        // 각 룸별 이미지 리스트 (Map<room_id, List<SharehouseRoomImgVO>>)
        Map<Long, List<SharehouseRoomImgVO>> roomImagesMap = new HashMap<>();
        for (SharehouseRoomVO room : rooms) {
            List<SharehouseRoomImgVO> imgs = sharehouseService.selectSharehouseRoomImages(room.getRoom_id());
            roomImagesMap.put(room.getRoom_id(), imgs);
        }
        model.addAttribute("roomImagesMap", roomImagesMap);

        // 근처 역/학교
        List<SharehouseSubwaysVO> subways = sharehouseService.selectSharehouseSubways(house_id);
        model.addAttribute("subways", subways);

        List<SharehouseSchoolsVO> schools = sharehouseService.selectSharehouseSchools(house_id);
        model.addAttribute("schools", schools);

        return "views/sharehouse/admin_detail";
    }

    // 수정폼 호출
    @GetMapping("/admin_modify/{house_id}")
    public String adminModifyForm(@PathVariable Long house_id, Model model) {
        // 쉐어하우스 기본 정보
        SharehouseVO sharehouse = sharehouseService.selectSharehouse(house_id);
        model.addAttribute("sharehouseVO", sharehouse);

        // 쉐어하우스 이미지 (HOUSE, STRUCTURE)
        model.addAttribute("houseImages", sharehouseService.selectSharehouseImages(house_id, "HOUSE"));
        model.addAttribute("structureImages", sharehouseService.selectSharehouseImages(house_id, "STRUCTURE"));

        // 룸 리스트 + 각 방 이미지
        List<SharehouseRoomVO> rooms = sharehouseService.selectSharehouseRooms(house_id);
        model.addAttribute("rooms", rooms);
        Map<Long, List<SharehouseRoomImgVO>> roomImagesMap = new HashMap<>();
        for (SharehouseRoomVO room : rooms) {
            roomImagesMap.put(room.getRoom_id(), sharehouseService.selectSharehouseRoomImages(room.getRoom_id()));
        }
        model.addAttribute("roomImagesMap", roomImagesMap);

        // 역, 학교
        model.addAttribute("subways", sharehouseService.selectSharehouseSubways(house_id));
        model.addAttribute("schools", sharehouseService.selectSharehouseSchools(house_id));

        return "views/sharehouse/admin_modify";
    }
    
    // 수정처리
    @PostMapping("/admin_modify/{house_id}")
    public String adminModify(
        @Valid @ModelAttribute("sharehouseVO") SharehouseVO sharehouseVO,
        BindingResult result,
        @RequestParam(required = false) List<SharehouseRoomVO> rooms,
        @RequestParam(required = false) List<SharehouseSubwaysVO> subways,
        @RequestParam(required = false) List<SharehouseSchoolsVO> schools,
        Model model, RedirectAttributes redirect
    ) {
        Long house_id = sharehouseVO.getHouse_id();

        // 유효성 검사
        if (result.hasErrors()) {
        	ValidationUtil.printErrorFields(result);
            model.addAttribute("houseImages", sharehouseService.selectSharehouseImages(house_id, "HOUSE"));
            model.addAttribute("structureImages", sharehouseService.selectSharehouseImages(house_id, "STRUCTURE"));
            List<SharehouseRoomVO> roomList = sharehouseService.selectSharehouseRooms(house_id);
            model.addAttribute("rooms", roomList);
            Map<Long, List<SharehouseRoomImgVO>> roomImagesMap = new HashMap<>();
            for (SharehouseRoomVO room : roomList) {
                roomImagesMap.put(room.getRoom_id(), sharehouseService.selectSharehouseRoomImages(room.getRoom_id()));
            }
            model.addAttribute("roomImagesMap", roomImagesMap);
            model.addAttribute("subways", sharehouseService.selectSharehouseSubways(house_id));
            model.addAttribute("schools", sharehouseService.selectSharehouseSchools(house_id));
            return "views/sharehouse/admin_modify";
        }
        
        // 메인(sharehouse) + 하위테이블(rooms, subways, schools 등) 동시 갱신
        sharehouseService.updateSharehouseAll(sharehouseVO, rooms, subways, schools);
        redirect.addFlashAttribute("msg", "수정되었습니다.");
        return "views/sharehouse/admin_detail";
    }

    // 삭제처리
    @PostMapping("/sharehouse/admin_delete/{house_id}")
    public String adminDelete(@PathVariable Long house_id, RedirectAttributes redirect) {
        sharehouseService.deleteSharehouse(house_id);
        redirect.addFlashAttribute("msg", "삭제되었습니다.");
        return "redirect:/sharehouse/admin_list";
    }
    
}