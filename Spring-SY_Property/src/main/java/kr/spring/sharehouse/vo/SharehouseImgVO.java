package kr.spring.sharehouse.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude={"sh_img"}) //출력하면 너무 길어서 제외시킴
public class SharehouseImgVO {
	
	private long house_id;
	private String sh_img_name;
	private byte[] sh_img;
	private int sh_img_seq;
	private String sh_img_type;	
	
} //class
