package com.carely.backend.dto.user;

import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import lombok.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class MapUserDTO {
    private Long userId;
    private String story;
    private UserType userType;
    private String city;
    private String address;
    private String detailAddress;
    private double latitude;
    private double longitude;

    public MapUserDTO toDTO(User user) {
        MapUserDTO mapUserDTO =  MapUserDTO.builder()
                .userId(user.getId())
                .story(user.getStory())
                .userType(user.getUserType())
                .city(user.getCity())
                .address(user.getAddress())
                .detailAddress(user.getDetailAddress())
                .build();

        getLocation(mapUserDTO);
        return mapUserDTO;
    }

    private void getLocation(MapUserDTO mapUserDTO) {

        String API_KEY = "78256822bbbbbe614142bcad43930708"; // 여기에 카카오 API 키 입력
        String GEOCODE_URL = "https://dapi.kakao.com/v2/local/search/address.json";

        String address = mapUserDTO.getAddress(); // 위도 경도를 구할 주소
        try {
            String urlStr = GEOCODE_URL + "?query=" + java.net.URLEncoder.encode(address, "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "KakaoAK " + API_KEY); // API Key 헤더 설정

            // API 호출 후 응답 받기
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 응답을 JSON으로 파싱
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject location = jsonResponse.getJSONArray("documents")
                    .getJSONObject(0)
                    .getJSONObject("address");

            double latitude = location.getDouble("y");  // 위도
            double longitude = location.getDouble("x"); // 경도

            System.out.println("Latitude: " + latitude);
            System.out.println("Longitude: " + longitude);

            mapUserDTO.setLatitude(latitude);
            mapUserDTO.setLongitude(longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

