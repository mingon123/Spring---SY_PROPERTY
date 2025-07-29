package kr.spring.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminOperationController {

    @GetMapping("/main/admin_operation")
    public String operationMain(Model model) {
        return "views/main/admin_operation";
    }

    @GetMapping("/main/admin_stats")
    public String statsMain(Model model) {
    	return "views/main/admin_stats";
    }
}
