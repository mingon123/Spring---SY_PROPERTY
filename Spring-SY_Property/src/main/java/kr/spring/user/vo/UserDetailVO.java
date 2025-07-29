package kr.spring.user.vo;

import java.io.IOException;
import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString(exclude = {"photo"})
public class UserDetailVO {
	private Long user_num;
	@NotBlank
	private String name;
	@NotBlank
	@Pattern(regexp="^01[016789]\\d{7,8}$")
	private String phone;
	@Email
	@NotBlank
	private String email;
	@Size(min=5,max=5)
	private String zipcode;
	@NotBlank
	private String address1;
	@NotBlank
	private String address2;
	private byte[] photo;
	private String photo_name;
	private Date reg_date;
	private Date modi_date;
	
	// 이미지 BLOB 처리
	public void setUpload(MultipartFile upload) throws IOException {
		log.debug("<<사용자 파일처리 진입>>");
		// MultipartFile -> byte[]
		setPhoto(upload.getBytes());
		// 파일 이름
		setPhoto_name(upload.getOriginalFilename());
	}
	
	// 전화번호 '-' 처리
	public void setPhone(String phone) {
	    if (phone != null) {
	        this.phone = phone.replaceAll("-", "");
	    } else {
	        this.phone = null;
	    }
	}
	
	public String getFormattedPhone() {
        if (phone == null || phone.length() < 10) return phone;
        if (phone.length() == 11) {
            return phone.replaceFirst("(\\d{3})(\\d{4})(\\d+)", "$1-$2-$3");
        } else if (phone.length() == 10) {
            return phone.replaceFirst("(\\d{2})(\\d{4})(\\d+)", "$1-$2-$3");
        }
        return phone;
    }
}
