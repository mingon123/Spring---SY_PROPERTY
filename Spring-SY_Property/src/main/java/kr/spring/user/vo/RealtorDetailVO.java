package kr.spring.user.vo;

import java.io.IOException;
import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString(exclude = {"photo", "certificate"})
public class RealtorDetailVO {
	private Long realtor_num;
	@NotBlank
	private String name;
	@NotBlank
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
	private MultipartFile upload;
	private Date reg_date;
	private Date modi_date;
	private byte[] certificate;
	private String certificate_name;
	private Date valid_date;
	private MultipartFile cerUpload;
	
	// 이미지 BLOB 처리
	public void setUpload(MultipartFile upload) {
	    this.upload = upload;
	    try {
	        setPhoto(upload.getBytes());
	        setPhoto_name(upload.getOriginalFilename());
	    } catch (IOException e) {
	        log.error("파일 처리 중 오류 발생", e);
	    }
	}
	
	public void setCerUpload(MultipartFile cerUpload) throws IOException {
		log.debug("<<중개사 자격증 파일처리 진입>>");
		this.cerUpload = cerUpload;
		if (cerUpload != null && !cerUpload.isEmpty()) {
			this.certificate = cerUpload.getBytes();
			this.certificate_name = cerUpload.getOriginalFilename();
		}
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
