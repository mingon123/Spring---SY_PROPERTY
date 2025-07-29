package kr.spring.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

// 자바코드 기반 설정 클래스
@Configuration
@EnableWebSocket
public class Emailconfig{

	@Value("${dataconfig.google-mail-url}")
	private String google_mail_url;
	
	@Value("${dataconfig.google-mail-password}")
	private String google_mail_password;

    public String getSenderEmail() {
        return google_mail_url;
    }

    public String getSenderName() {
        return "BangBangGo";
    }
	
	@Bean
	public JavaMailSenderImpl javaMailSenderImpl() {
		Properties prop = new Properties();
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.transport.protocol", "smtp");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.debug", "true");
		
		JavaMailSenderImpl javaMail = new JavaMailSenderImpl();
		javaMail.setHost("smtp.gmail.com");
		javaMail.setPort(587);
		javaMail.setDefaultEncoding("utf-8");
		javaMail.setUsername(google_mail_url);
		javaMail.setPassword(google_mail_password);
		javaMail.setJavaMailProperties(prop);		
		return javaMail;
	}

}
