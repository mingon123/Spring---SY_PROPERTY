package kr.spring.sharehouse.vo;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class SharehouseSchoolsVO {
	
	private long house_id;
	private String school_name;
	private float distance;	
	
} //class
