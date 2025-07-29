package kr.spring.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import kr.spring.chat.service.ChatService;
import kr.spring.chat.vo.ChatMessageVO;
@RestController
@RequestMapping("/api/chat")
public class ChatMessageRestController {

	@Autowired
	private ChatService chatService;

	@PostMapping("/messages")
	public ResponseEntity<String> sendMessage(@RequestBody ChatMessageVO msg) {
		chatService.insertChat(msg);
		return ResponseEntity.ok("메시지 저장 완료!");
	}

	@GetMapping("/messages/{chatroom_num}")
	public ResponseEntity<List<ChatMessageVO>> getMessages(@PathVariable Long chatroom_num) {
		return ResponseEntity.ok(chatService.getMessagesByRoom(chatroom_num));
	}
	
	@GetMapping("/messages")
	@ResponseBody
	public List<ChatMessageVO> getMessageByRoom(@RequestParam("chatroom_num") Long chatroom_num){
		return chatService.getMessagesByRoom(chatroom_num);
	}
}
