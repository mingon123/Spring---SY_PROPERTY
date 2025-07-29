package kr.spring.sharehouse.vo;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class SharehouseSubwaysVO {
	
	private long house_id;
	private String station_name;
	private String line_short;
	private float distance;	
	
} //class
