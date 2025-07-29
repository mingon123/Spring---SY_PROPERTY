package kr.spring.sharehouse.vo;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import kr.spring.user.vo.UserVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SharehouseVO {
	
	private long house_id;
	@NotBlank
	private String name;
	private int s_state;
	private int s_hit;
	private Date s_reg_date;
	private Date s_modi_date;
	private String address;
	private String sido;
	private String sigungu;
	private String dong;
	private String latitude;
	private String longitude;
	private String floor_info;
	private String gender_type_cd;
    private int deposit_min;
    private int deposit_max;
    private float price_min;
    private float price_max;
    private String maintenance_fees;
    private int enter_age_min;
    private int enter_age_max;
    private String title_image;
    private String structure;
    @NotBlank
    private String house_info;         // CLOB -> String
    private String move_requirements;  // CLOB -> String
    private String operator;
    private Long realtor_num;	
    
    //목록 출력용 가공정보 from sharehouse_room 테이블
    private float fee_min;  // maintenance_fee 최소값
    private float fee_max;  // maintenance_fee 최대값
    private Integer stay_min; // duration_min의 최소값
    private Integer stay_maxMax; // duration_max의 최대값
    private Integer stay_maxMin; // duration_max의 최소값
    
	//private int fav_cnt; //좋아요 개수(필요시)
    		
	//통째로 받음
	private UserVO userVO;
	private SharehouseRoomVO sharehouseRoomVO;
	private SharehouseSubwaysVO sharehousSubwaysVO;
	private SharehouseSchoolsVO sharehouseSchoolsVO;
	
	//층수변환
	public String getFloorInfo() {
	    if (floor_info == null || !floor_info.contains("|")) return "";

	    String[] parts = floor_info.split("\\|");
	    String totalFloor = parts[0]; // 전체 층

	    String[] usedFloors = parts[1].split(",");
	    int min = Integer.MAX_VALUE;
	    int max = Integer.MIN_VALUE;

	    for (String floor : usedFloors) {
	        try {
	            int f = Integer.parseInt(floor.trim());
	            min = Math.min(min, f);
	            max = Math.max(max, f);
	        } catch (NumberFormatException e) {}
	    }

	    String minFloor = (min < 0) ? "지하" + (-min) + "층" : min + "층";
	    String maxFloor = (max < 0) ? "지하" + (-max) + "층" : max + "층";

	    String usedRange = min == max ? minFloor : (minFloor + "-" + maxFloor);

	    return "사용 " + usedRange + " / 전체 " + totalFloor + "층";
	}

    
} //class
