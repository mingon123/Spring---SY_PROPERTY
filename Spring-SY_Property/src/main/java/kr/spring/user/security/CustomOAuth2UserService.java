package kr.spring.user.security;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import kr.spring.user.service.UserService;
import kr.spring.user.vo.PrincipalDetails;
import kr.spring.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		log.debug("[OAuth2 로그인] loadUser 진입");
		// 기본적으로 사용자 정보 불러오기
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String provider = userRequest.getClientRegistration().getRegistrationId();
		Map<String, Object> attributes = oAuth2User.getAttributes();

        log.debug("provider : {}", provider);
        log.debug("attributes : {}", attributes);
		
        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            String nickname = (String) profile.get("nickname");
            
            // 유저 식별용 고유 ID 생성
            String socialId = "kakao_" + attributes.get("id");
            // DB에서 기존 사용자 확인
            UserVO user = userService.selectCheckUser(socialId);
            
            if (user == null) {
                user = new UserVO();
                user.setUser_num(userService.selectUserNum());
                user.setId(socialId);
                user.setPasswd(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setUser_type("USER");
                user.setAuthority("ROLE_USER");
                user.setOauth(1);
                user.setReport_count(0);
                String baseNick = nickname;
                int count = 1;
                while (userService.selectCheckNickName(nickname) != null) {
                    nickname = baseNick + "_" + count;
                    count++;
                }
                user.setNick_name(nickname);
                
                log.debug("신규 유저 insert: {}", user);
                userService.insertSocialUser(user);
            } else {
                log.debug("기존 유저 로그인: {}", user);
            }
            log.debug("최종 반환할 PrincipalDetails: {}", user);
            return new PrincipalDetails(user, attributes);
        }
        
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");

            String socialId = "naver_" + response.get("id");
            String nickname = (String) response.get("nickname");

            UserVO user = userService.selectCheckUser(socialId);

            if (user == null) {
                user = new UserVO();
                user.setUser_num(userService.selectUserNum());
                user.setId(socialId);
                user.setPasswd(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setUser_type("USER");
                user.setAuthority("ROLE_USER");
                user.setOauth(1);
                user.setReport_count(0);

                // 닉네임 중복 처리
                String baseNick = nickname;
                int count = 1;
                while (userService.selectCheckNickName(nickname) != null) {
                    nickname = baseNick + "_" + count;
                    count++;
                }
                user.setNick_name(nickname);

                log.debug("신규 네이버 유저 insert: {}", user);
                userService.insertSocialUser(user);
            } else {
                log.debug("기존 네이버 유저 로그인: {}", user);
            }
            return new PrincipalDetails(user, attributes);
        }
        
        
		log.warn("지원하지 않는 provider : {}", provider);
		// 다른 provider 추가 가능
		return oAuth2User;
	}
}
