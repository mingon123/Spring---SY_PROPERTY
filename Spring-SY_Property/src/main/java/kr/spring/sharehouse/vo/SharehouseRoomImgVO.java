package kr.spring.sharehouse.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude={"shr_img"}) //출력하면 너무 길어서 제외시킴
public class SharehouseRoomImgVO {

	private long room_id;
	private String shr_img_name;
	private byte[] shr_img;
	private int shr_img_seq;
	
} //class
