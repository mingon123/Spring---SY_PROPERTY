package kr.spring.sharehouse.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SharehouseRoomVO {
	
	private long room_id;
	private long house_id;
	private String room_name;
	private int room_state;
	private String capacity;
	private int deposit;
	private float monthly_price;
	private int maintenance_fee;
	private int duration_min;
	private int duration_max;
	private String options;
	private String room_info;
		
} //class
