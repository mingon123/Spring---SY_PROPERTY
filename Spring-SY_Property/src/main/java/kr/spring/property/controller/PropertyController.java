package kr.spring.property.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.property.service.PropertyService;
import kr.spring.property.vo.PropertyVO;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.util.PagingUtil;

@Controller
@RequestMapping("/property")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/category")
    public String searchByCategory(@RequestParam("type") String category, Model model) {
        List<PropertyVO> propertyList = propertyService.searchByCategory(category);
        model.addAttribute("propertyList", propertyList);
        model.addAttribute("selectedCategory", category); // 선택된 카테고리도 넘김
        return "views/map/mapMain"; // 지도로 매물 띄우는 페이지
    }
 
    
    @GetMapping("/favorite")
    public String getFavoriteProperties(
            @RequestParam(value="pageNum", defaultValue = "1") int currentPage,
            @AuthenticationPrincipal PrincipalDetails principal,
            Model model) {

        if (principal == null) return "redirect:/user/loginRegisterUser";

        Long userNum = principal.getUserVO().getUser_num();
        
        int rowCount = 6; // 한 페이지에 6개 보여줌
        int pageCount = 5; // 하단에 5개 페이지 링크

        int total = propertyService.getFavCount(userNum); // 전체 좋아요 매물 수

        PagingUtil page = new PagingUtil(currentPage, total, rowCount, pageCount, "/property/favorite");

        List<PropertyVO> favList = propertyService.getFavPropertyListPaging(userNum, page.getStartRow(), page.getEndRow());

        model.addAttribute("properties", favList);
        model.addAttribute("page", page.getPage());

        return "views/user/userFavList";
    }

    // 좋아요한 매물 상세 페이지 출력
    @GetMapping("/favDetail/{id}")
    public String viewFavoriteDetail(@PathVariable("id") Long propertyId, Model model) {
        PropertyVO property = propertyService.getPropertyDetail(propertyId);
        model.addAttribute("property", property);
        return "views/user/userFavDetail"; 
    }
    
    // 만드는 중 (희동)
    // @PostMapping("/recent/view")
    // @ResponseBody
    // public void addRecentView(@RequestParam("property_id"))
    


}
