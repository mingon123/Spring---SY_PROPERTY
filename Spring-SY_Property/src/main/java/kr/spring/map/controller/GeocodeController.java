package kr.spring.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GeocodeController {

    private final RestTemplate restTemplate;

    private static final String KAKAO_API_KEY = "KakaoAK dd754883945341d00160998327eb5f7e";  // 자신의 REST API 키로 교체

    @GetMapping("/geocode")
    public ResponseEntity<String> getCoords(@RequestParam("query") String query) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", query)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", KAKAO_API_KEY);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return ResponseEntity.ok(response.getBody());
    }
}
