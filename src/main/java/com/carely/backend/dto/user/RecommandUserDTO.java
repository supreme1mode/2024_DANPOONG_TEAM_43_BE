package com.carely.backend.dto.user;

import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.service.kakao.KakaoAddressService;
import lombok.*;

import java.util.Map;

public class RecommandUserDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private Long userId;
        private String username;
        private UserType userType;
        private Long timeTogether; // 새로운 필드
        private String address;
        @Setter
        private double latitude;
        @Setter
        private double longitude;
        @Setter
        private double km;

        public static RecommandUserDTO.Res toDTO(User user, long timeTogether,  Double latitude, Double longittude) {
            Res res =  Res.builder()
                    .userId(user.getId())
                    .address(user.getAddress())
                    .username(user.getUsername())
                    .userType(user.getUserType())
                    .timeTogether(timeTogether)
                    .build();

            getLocation(res);
            res.setKm(calculateDistance(res.getLatitude(), res.getLatitude(), latitude, latitude));
            return res;
        }

        private static void getLocation(Res mapUserDTO) {
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
}
