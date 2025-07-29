package kr.spring.property.controller;

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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.property.service.PropertyEditRequestService;
import kr.spring.property.service.PropertyService;
import kr.spring.property.vo.PropertyEditRequestVO;
import kr.spring.property.vo.PropertyVO;
import kr.spring.util.PagingUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/realtor")
public class PropertyAdminController {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private PropertyEditRequestService propertyEditRequestService;

    // 매물 목록
    @GetMapping("/admin_propertyList")
    public String list(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum, Model model) {
    	log.debug("<<관리자 매물 목록 >>");
    	int count = propertyService.selectRowCount();

        PagingUtil page = new PagingUtil(null, null, pageNum, count, 10, 10, "admin_propertyList", null);

        List<PropertyVO> list = null;
        if (count > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("start", page.getStartRow());
            map.put("end", page.getEndRow());
            list = propertyService.selectList(map);
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("page", page.getPage());

        return "views/realtor/admin_propertyList";
    }

    // 매물 상세
    @GetMapping("/admin_propertyDetail/{property_id}")
    public String detail(@PathVariable Long property_id, Model model) {
    	log.debug("<<관리자 매물 상세>> : " + property_id);
        PropertyVO property = propertyService.getPropertyDetail(property_id);
        List<PropertyEditRequestVO> requests = propertyEditRequestService.getRequestsByPropertyId(property_id);
        model.addAttribute("property", property);
        model.addAttribute("requests", requests);
        return "views/realtor/admin_propertyDetail";
    }
    
    // 매물 수정
    @PostMapping("/admin_propertyDetail/{property_id}")
    public String update(@PathVariable Long property_id,
            @Valid @ModelAttribute PropertyVO property,
            BindingResult result, Model model, HttpServletRequest request) {
        log.debug("<<관리자 매물 수정>> : " + property_id);

        if (result.hasErrors()) {
        	ValidationUtil.printErrorFields(result);
            List<PropertyEditRequestVO> requests = propertyEditRequestService.getRequestsByPropertyId(property_id);
            model.addAttribute("requests", requests);
            return "views/realtor/admin_propertyDetail";
        }
        
        property.setProperty_id(property_id);
        propertyService.updateProperty(property);

		model.addAttribute("accessTitle", "매물정보 수정");
		model.addAttribute("accessMsg", "매물정보 수정 완료");
		model.addAttribute("accessUrl", request.getContextPath() + "/realtor/admin_propertyDetail/" + property_id);
		model.addAttribute("accessBtn", "이동");
		
		return "views/common/resultView";
    }
    
    // 매물 삭제
    @PostMapping("/admin_propertyDelete/{property_id}")
    public String deleteProperty(@PathVariable Long property_id, Model model) {
        log.debug("<<관리자 매물 삭제>> : " + property_id);
        propertyService.deleteProperty(property_id);

        model.addAttribute("accessTitle", "매물 삭제");
		model.addAttribute("accessMsg", "매물이 삭제되었습니다.");
		model.addAttribute("accessUrl", "/realtor/admin_propertyList");
		model.addAttribute("accessBtn", "목록");
		return "views/common/resultView";
    }
   
}
