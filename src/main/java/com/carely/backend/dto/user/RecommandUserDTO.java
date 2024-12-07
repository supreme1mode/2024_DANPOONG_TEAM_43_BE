package com.carely.backend.dto.user;

import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import lombok.*;

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
        private String talk;
        private String eat;
        private String toilet;
        private String bath;
        private String walk;

        public static RecommandUserDTO.Res toDTO(User user, long timeTogether, User currentUser) {
            Res res =  Res.builder()
                    .userId(user.getId())
                    .address(user.getAddress())
                    .username(user.getUsername())
                    .userType(user.getUserType())
                    .timeTogether(timeTogether)
                    .talk(user.getTalk())
                    .eat(user.getEat())
                    .toilet(user.getEat())
                    .bath(user.getBath())
                    .walk(user.getWalk())
                    .latitude(user.getLatitude())
                    .longitude(user.getLongitude())
                    .build();

            res.setKm(calculateDistance(currentUser.getLatitude(), currentUser.getLongitude(), user.getLatitude(), user.getLongitude()));
            return res;
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
