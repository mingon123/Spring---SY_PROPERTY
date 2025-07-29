package kr.spring.chat.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.spring.chat.dao.ChatMapper;
import kr.spring.chat.service.ChatService;
import kr.spring.chat.vo.ChatRoomVO;
import kr.spring.user.service.UserService;
import kr.spring.user.vo.UserRole;
import kr.spring.user.vo.UserVO;
import kr.spring.util.PagingUtil;

@Controller // 이 클래스가 MVC Controller 역할을 한다는 표시
@RequestMapping("/chat") // 공통 URL prefix
public class ChatroomController {

   @Autowired
   private ChatService chatService;
   @Autowired
   private UserService userService;
   @Autowired
   private ChatMapper chatMapper;

   
    @PostMapping("/chatroom/create")
    @ResponseBody
    public ChatRoomVO createRoom(@RequestParam("user_num") long user_num,
           @RequestParam("realtor_num") long realtor_num,
           @RequestParam("property_num") long property_num) {
       return chatService.createRoom(user_num,realtor_num,property_num);
    }
    
    @GetMapping("/popup")
    public String loadChatPopup(@RequestParam(name = "chatroom_num") Long chatroom_num,
                                Principal principal, Model model) {
        String username = principal.getName();
        UserVO user = userService.selectCheckUser(username);
        Long user_num = user.getUser_num();

        model.addAttribute("chatroom_num", chatroom_num);
        model.addAttribute("user_num", user_num);

        return "fragments/chatPopup"; // templates/fragments/chatPopup.html
    }
    
    @GetMapping("/myrooms")
    @ResponseBody
    public List<ChatRoomVO> getMyChatrooms(Principal principal) {
        String username = principal.getName();
        UserVO user = userService.selectCheckUser(username);
        return chatService.getChatroomsByUserNum(user.getUser_num());
    }
    

    //  ChatroomController.java
    @GetMapping("/list")
    public String chatroomList(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
                            Model model, Principal principal) {
       if (principal == null) {
      return "redirect:/login";
      }

       String username = principal.getName();
       UserVO user = userService.selectCheckUser(username);
       long user_num = user.getUser_num();
       int authority = user.getAuthorityOrdinal();

       List<ChatRoomVO> chatrooms;
       int count;
  
       boolean isRealtor = authority == UserRole.REALTOR.ordinal();

       if (isRealtor) {
          count = chatService.getChatroomCountRealtor(user_num);
       } else {
          count = chatService.getChatroomCountUser(user_num);
       }

       PagingUtil page = new PagingUtil(currentPage, count, 10, 10, "/chat/list");

       if (isRealtor) {
         chatrooms = chatService.getChatroomsRealtorPage(user_num, page.getStartRow(), page.getEndRow());
       } else {
         chatrooms = chatService.getChatroomsUserPage(user_num, page.getStartRow(), page.getEndRow());
     }

       model.addAttribute("chatrooms", chatrooms);
       model.addAttribute("user_num", user_num);
       model.addAttribute("isRealtor", isRealtor);
       model.addAttribute("page", page.getPage());

       return "views/chat/chatroomList";
    }
    
    
    @RequestMapping(value = "/chatroom/checkOrCreate", method = RequestMethod.POST)
    @ResponseBody
    public ChatRoomVO checkOrCreateChatroom(@RequestParam("user_num") long user_num,
                                            @RequestParam("realtor_num") long realtor_num,
                                            @RequestParam("property_num") long property_num) {
        // 채팅방 존재 여부 확인
        ChatRoomVO room = chatService.findChatRoom(user_num, realtor_num, property_num);
        System.out.println("=== 채팅방 생성 요청 ===");
        System.out.println("user_num: " + user_num);
        System.out.println("realtor_num: " + realtor_num);
        System.out.println("property_num: " + property_num);
        
        try {
            room = chatService.findChatRoom(user_num, realtor_num, property_num);
            if (room == null) {
                room = chatService.createRoom(user_num, realtor_num, property_num);
            }
        } catch (Exception e) {
            e.printStackTrace();  
            throw new RuntimeException("채팅방 생성 중 오류 발생", e);
        }

        return room; // chatroom_num 포함된 객체 반환
    }
}
