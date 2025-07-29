package kr.spring.property.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PropertyFavVO {
	private long property_id; 
	private long user_num; 
	private int alarm_flag;
}