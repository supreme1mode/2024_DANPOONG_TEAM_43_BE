package com.carely.backend.dto.user;


import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    // 카카오 id
    @NotNull
    private String kakaoId;
    // 타입
    @NotNull
    private UserType userType;
    // 이름
    @NotNull
    private String username;
    // 전화번호
    @NotNull
    private String phoneNum;
    // 주소
    @NotNull
    private String address;
    // 상세 주소
    @NotNull
    private String detailAddress;

    // 위치 인증 여부
    @NotNull
    private Boolean locationAuthentication;

    @NotNull
    private String talk;
    @NotNull
    private String eat;
    @NotNull
    private String toilet;
    @NotNull
    private String bath;
    @NotNull
    private String walk;
    @NotNull
    private String story;
    // 내 위치 공유
    @NotNull
    private Boolean shareLocation;

    private String identity;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        // 아ㅣ디
        private Long userId;
        // 카카오 id
        private String kakaoId;
        // 타입
        private UserType userType;
        // 이름
        private String username;
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
        // 나이
        private Integer age;

        private Boolean shareLocation;
        private Double latitude;
        private Double longitude;


        public static Res toDTO(User user) {
            return Res.builder()
                    .age(user.getAge())
                    .userId(user.getId())
                    .kakaoId(user.getKakaoId())
                    .city(user.getCity())
                    .userType(user.getUserType())
                    .username(user.getUsername())
                    .phoneNum(user.getPhoneNum())
                    .address(user.getAddress())
                    .detailAddress(user.getDetailAddress())
                    .locationAuthentication(user.getLocationAuthentication())
                    .shareLocation(user.getShareLocation())
                    .latitude(user.getLatitude())
                    .longitude(user.getLongitude())
                    .build();
        }
    }

}
