package kr.spring.qna.vo;

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
public class QnaVO {
	private long qna_num;
	@NotBlank
	private String title;
	@NotEmpty
	private String content;
	private Date reg_date;
	private Date modi_date;
	private byte[] photo;
	private String photo_name;
	
	private MultipartFile upload;
	
	@NotBlank
	private String category;
	
	//관리자의 답변상태
	private String answer_status;
	//관리자의 답변내용
	private String answer;
	
	private long user_num;
}







