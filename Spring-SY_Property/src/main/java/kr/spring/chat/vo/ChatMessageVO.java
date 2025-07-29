package kr.spring.chat.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageVO {

	public enum MessageType{
		ENTER, CHAT, LEAVE
	}
	
	 private Long chat_num;
	 private Long chatroom_num;
	 private Long user_num;
	 private String message;
	 private Date chat_date;
	 private int read_check = 1;
	
}
