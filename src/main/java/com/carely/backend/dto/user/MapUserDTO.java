package com.carely.backend.dto.user;

import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.service.kakao.KakaoAddressService;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Setter
    private Double km;

    public MapUserDTO toDTO(User user, Long togetherTime, User currentUser) {
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
                .longitude(user.getLongitude())
                .latitude(user.getLatitude())
                .build();

        if (!user.getShareLocation()) {
            // shareLocation이 false라면 가장 가까운 마을 센터나 요양 병원의 위치를 가져옴
            double[] nearestLocation = findNearestCareCenter(user.getAddress());
            mapUserDTO.setLatitude(nearestLocation[0]);
            mapUserDTO.setLongitude(nearestLocation[1]);
        }

        mapUserDTO.setKm(calculateDistance(currentUser.getLatitude(), currentUser.getLongitude(), user.getLatitude(), user.getLongitude()));
        return mapUserDTO;
    }

    // 가장 가까운 마을 센터나 요양 병원 좌표를 찾는 메서드
    private double[] findNearestCareCenter(String address) {
        try {
            String apiUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";
            String apiKey = "78256822bbbbbe614142bcad43930708";

            // 요양센터 검색
            String keyword = URLEncoder.encode(address  + "요양 센터", "UTF-8");
            // String keyword = address + " 요양 센터";
            URL url = new URL(apiUrl + "?query=" + keyword);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "KakaoAK " + apiKey);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.getJSONArray("documents").length() > 0) {
                JSONObject location = jsonResponse.getJSONArray("documents").getJSONObject(0);
                double latitude = location.getDouble("y"); // 위도
                double longitude = location.getDouble("x"); // 경도
                return new double[]{latitude, longitude};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 실패 시 카카오 AI 캠퍼스
        return new double[]{37.351544, 127.070358};
    }

    // 거리 계산
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0; // 지구 반지름 (단위: km)

        // 위도, 경도를 라디안으로 변환
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // 위도, 경도 차이 계산
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // 하버사인 공식
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리 계산 (단위: km)
        return R * c;
    }
}


