package com.carely.backend.service.kakao;

import com.carely.backend.exception.KakaoException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAddressService {

    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String GEOCODE_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    @Value("${kakao.apiKey}")
    private String apiKey = "78256822bbbbbe614142bcad43930708";

    public String getAddressDetails(String address) {
        try {
            System.out.println(apiKey);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + apiKey);

            String url = KAKAO_API_URL + "?query=" + address;
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new KakaoException("카카오 API 호출 중 오류가 발생했습니다.");
        }

    }

    // 주소로부터 위도, 경도 가져오기
    public Map<String, Double> getLocationFromAddress(String address) {
        Map<String, Double> location = new HashMap<>();

        try {
            String urlStr = GEOCODE_URL + "?query=" + java.net.URLEncoder.encode(address, "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "KakaoAK " + apiKey); // API Key 헤더 설정

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // JSON 응답 처리
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject locationData = jsonResponse.getJSONArray("documents").getJSONObject(0).getJSONObject("address");

            double latitude = locationData.getDouble("y"); // 위도
            double longitude = locationData.getDouble("x"); // 경도

            location.put("latitude", latitude);
            location.put("longitude", longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
}
