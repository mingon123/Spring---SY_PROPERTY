package kr.spring.faq.vo;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"content"})
public class FaqVO {
	private long faq_num;
	@NotBlank
	private String title;
	@NotEmpty
	private String content;
	private Date reg_date;
}







