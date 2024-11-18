package com.carely.backend.dto.user;


import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String userType;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Group {
        private Long userId;
        private String username;
        private UserType userType;
        private Long timeTogether; // 새로운 필드

        public static Group toDTO(User user, long timeTogether) {
            return Group.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .userType(user.getUserType())
                    .timeTogether(timeTogether)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentUser {
        private Long userId;
        private String username;
        private com.carely.backend.domain.enums.UserType userType;
        private String story;

        public static RecentUser toDTO(User user) {
            return RecentUser.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .userType(user.getUserType())
                    .story(user.getStory())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Verification {
        private Long userId;
        private String username;
        private com.carely.backend.domain.enums.UserType userType;
        // 위치 인증 여부
        private Boolean locationAuthentication;

        public static Verification toDTO(User user) {
            return Verification.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .userType(user.getUserType())
                    .locationAuthentication(user.getLocationAuthentication())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationAddress {
        private Long userId;
        private String username;
        private UserType userType;
        // 위치 인증 여부
        private Boolean locationAuthentication;
        private String address;
        private String detailAddress;


        public static VerificationAddress toDTO(User user) {
            return VerificationAddress.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .userType(user.getUserType())
                    .locationAuthentication(user.getLocationAuthentication())
                    .address(user.getAddress())
                    .detailAddress(user.getDetailAddress())
                    .build();
        }
    }
}
