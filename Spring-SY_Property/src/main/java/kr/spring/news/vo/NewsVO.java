package kr.spring.news.vo;

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
public class NewsVO {
	private long news_num;
	@NotBlank
	private String title;
	@NotEmpty
	private String content;
	private Date reg_date;
	private byte[] photo;
	private String photo_name;
	
	private MultipartFile upload;
}







