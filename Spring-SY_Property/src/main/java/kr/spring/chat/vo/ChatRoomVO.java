package kr.spring.chat.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomVO {

	private long chatroom_num;
	private long user_num;
	private long realtor_user_num;
	private Date room_date;
	
	private String chatroom_title;
	private long property_num;

}
