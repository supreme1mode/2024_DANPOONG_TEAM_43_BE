package com.carely.backend.dto.user;

import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import lombok.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;


@Getter
public class MyPageDTO {
    private Long userId;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailRes {
        private Long userId;
        // 이름
        private String username;
        // 나이
        private Integer age;
        // 전화번호
        private String phoneNum;
        // 도시
        private String city;
        // 주소
        private String address;
        // 상세 주소
        private String detailAddress;
        // 위치 인증 여부
        private Boolean locationAuthentication;
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
        private String story;

        // 함께한 사람
        // 함께한 시간 추가해야 함

        public static DetailRes toDTO(User user) {
            DetailRes detailRes = DetailRes.builder()
                    .userId(user.getId())
                    .userType(user.getUserType())
                    .username(user.getUsername())
                    .age(user.getAge())
                    .phoneNum(user.getPhoneNum())
                    .locationAuthentication(user.getLocationAuthentication())
                    .shareLocation(user.getShareLocation())
                    .address(user.getAddress())
                    .detailAddress(user.getDetailAddress())
                    .city(user.getCity())
                    .talk(user.getTalk())
                    .eat(user.getEat())
                    .toilet(user.getToilet())
                    .bath(user.getBath())
                    .walk(user.getWalk())
                    .story(user.getStory())
                    .build();

            getLocation(detailRes);
            return detailRes;
        }

        private static void getLocation(DetailRes detailRes) {

            String API_KEY = "78256822bbbbbe614142bcad43930708";
            String GEOCODE_URL = "https://dapi.kakao.com/v2/local/search/address.json";

            String address = detailRes.getAddress();
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

                detailRes.setLatitude(latitude);
                detailRes.setLongitude(longitude);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
