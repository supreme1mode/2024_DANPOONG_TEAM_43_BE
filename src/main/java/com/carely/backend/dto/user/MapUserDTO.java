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

    public MapUserDTO toDTO(User user, Long togetherTime,  Double latitude, Double longittude) {
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
        mapUserDTO.setKm(calculateDistance(mapUserDTO.getLatitude(), mapUserDTO.getLatitude(), latitude, latitude));
        return mapUserDTO;
    }

    private void getLocation(MapUserDTO mapUserDTO) {
        KakaoAddressService kakaoAddressService = new KakaoAddressService();
        Map<String, Double> location = kakaoAddressService.getLocationFromAddress(mapUserDTO.getAddress());

        // 위도, 경도 설정
        Double latitude = location.get("latitude");
        Double longitude = location.get("longitude");

        mapUserDTO.setLatitude(latitude != null ? latitude : 0.0); // 기본값 0.0 사용
        mapUserDTO.setLongitude(longitude != null ? longitude : 0.0); // 기본값 0.0 사용

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


