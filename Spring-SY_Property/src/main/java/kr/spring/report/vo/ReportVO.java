package kr.spring.report.vo;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"content"})
public class ReportVO {
	//신고번호
	private long report_num;
	//신고자번호
	private long reporter_num;
	//신고대상자번호
	private long suspect_num;
	@NotBlank
	private String title;
	@NotEmpty
	private String content;
	private Date report_date;
	private byte[] r_photo;
	private String r_photo_name;
	
	private MultipartFile upload;
	
	//신고분류번호
	private Long report_type_num;
	private String report_type_name;
	// 신고 대상자 아이디
	private String suspect_user_id; 

	public String getSuspect_user_id() { return suspect_user_id; }
	public void setSuspect_user_id(String suspect_user_id) { this.suspect_user_id = suspect_user_id; }


	
}







