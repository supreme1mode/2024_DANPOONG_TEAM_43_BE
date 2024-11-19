package com.carely.backend.dto.user;

import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    // 이름
    private String username;
    // 도시
    private String city;
    // 주소
    private String address;
    // 상세 주소
    private String detailAddress;
    // 타입
    private UserType userType;
    // 내 위치 공유 여부
    private Boolean shareLocation;
    // 위도 경도
    @Setter
    private double latitude;
    @Setter
    private double longitude;

    private String talk;
    private String eat;
    private String toilet;
    private String bath;
    private String walk;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Setter
    private Long togetherTime;

    public MapUserDTO toDTO(User user, Long togetherTime) {
        MapUserDTO mapUserDTO =  MapUserDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .shareLocation(user.getShareLocation())
                .talk(user.getTalk())
                .eat(user.getEat())
                .toilet(user.getToilet())
                .bath(user.getBath())
                .walk(user.getWalk())
                .userType(user.getUserType())
                .city(user.getCity())
                .address(user.getAddress())
                .detailAddress(user.getDetailAddress())
                .togetherTime(togetherTime)
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


