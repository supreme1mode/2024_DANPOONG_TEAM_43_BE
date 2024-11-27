package com.carely.backend.service.kakao;

import com.carely.backend.domain.User;
import com.carely.backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    public Map<String, String> getKakaoUserInfo(String code) throws JsonProcessingException {
        String accessToken = getAccessToken(code);

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);  // Bearer 토큰 인증 설정

        HttpEntity<String> request = new HttpEntity<>(headers);

        // 사용자 정보 요청
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String userId = jsonNode.get("id").asText();
                String nickname = jsonNode.path("kakao_account").path("profile").path("nickname").asText();

                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("kakaoId", userId);
                userInfo.put("nickname", nickname);
                return userInfo;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse user info response", e);
            }
        } else {
            throw new RuntimeException("Failed to retrieve user info");
        }
    }

    public String getAccessToken(String code) throws JsonProcessingException {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디를 MultiValueMap으로 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId); // client_id
        params.add("redirect_uri", "https://carely-seven.vercel.app/kakao-login"); // redirect_uri
        params.add("code", code); // 인증 코드                          

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // POST 요청을 보내고 응답 받기
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String accessToken = jsonNode.get("access_token").asText();
            System.out.println("Access Token: " + accessToken);
            return accessToken;
        } else {
            throw new RuntimeException("Failed to retrieve access token");
        }
    }


    public User findUserByKakaoId(String kakaoId) {
        Optional<User> user = userRepository.findByKakaoId(kakaoId);
        if (user.isPresent()) {
            return user.get();
        }

        return null;
    }
}
