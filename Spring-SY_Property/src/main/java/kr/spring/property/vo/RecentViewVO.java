package kr.spring.property.vo;

import java.util.Date;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class RecentViewVO {

	    private Long recent_id;
	    private Long user_num;
	    private Long property_id;
	    private Date view_date;

}
