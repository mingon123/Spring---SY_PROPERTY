package kr.spring.property.vo;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PropertyVO {
	private long property_id; 
	private String room_number; //매물 번호(=외부 ID)
	@NotBlank
	private String title; //매물 제목
	@NotBlank
	private String address; // 주소
	@NotBlank
	private String room_type; //방 종류(ex:원룸, 투룸 등)
	@NotBlank
	private String price;//보증금, 월세 등 가격 정보
	@NotNull
	private double area_m2; //면적(m^2)
	@NotNull
	private int bed_count; //방 수
	@NotNull
	private int bathroom_count; //화장실 수
	@NotBlank
	private String floor_info; //층 수 정보(ex. 2층)
	@NotBlank
	private String building_floors; //건물 총 층수(ex. 4층)
	@NotBlank
	private String parking; //주차 가능 여부
	
	private String maintenance_cost; //관리비
	private String move_in_date; //입주 가능일
	private String approval_date; //사용 승인일
	private String complex_name; //단지명 또는 건물명
	private long household_num; //총 세대수
	private double exclusive_area_m2; //전용면적 (m^2)
	private double supply_area_m2;  // 공급 면적 (m^2)
	private String hashtags; // 해시태그들
	private String options; // 옵션 목록(ex: 냉장고 세탁기 등)
	
	private double latitude; //위도
	private double longitude; //경도
	
	private String category; //매물 유형(ex: 빌라, 오피스텔 등)
	private int keyp_state; //매물 상태(0:거래중 . 1: 거래완료)
	private long p_hit; //조회수
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date p_reg_date; //매물 등록일
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date p_modi_date; //매물 수정일

	private String status; // 대기, 승인, 거절
	// 중개사 번호 (어떤 중개사가 매물을 등록했는지 확인을 위해서)
	private long realtor_num;
	
	private int is_favorite;
}
