package com.carely.backend.domain;

import com.carely.backend.domain.common.BaseEntity;
import com.carely.backend.domain.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;


@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SuperUser")  // SuperUser 테이블 사용
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 카카오 id
    private String kakaoId;
    // 이름
    private String username;
    // 역할
    private String role;
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

    // ai 요약
    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<JoinGroup> joinGroups = new HashSet<>();

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Volunteer> volunteers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();


    private String talk;
    private String eat;
    private String toilet;
    private String bath;
    private String walk;
    private String story;

    // 회원가입
    @Builder(builderMethodName = "userBuilder")
    public User(String kakaoId, UserType userType, String role, String username, Integer age, String phoneNum,
                String city, String address, String detailAddress, Boolean locationAuthentication, Boolean shareLocation,
                String talk, String eat, String toilet, String bath, String walk, String story
    ) {
        this.kakaoId = kakaoId;
        this.userType = userType;
        this.username = username;
        this.role = role;
        this.age = age;
        this.phoneNum = phoneNum;
        this.city = city;
        this.address = address;
        this.detailAddress = detailAddress;
        this.locationAuthentication = locationAuthentication;
        this.shareLocation = shareLocation;
        this.talk = talk;
        this.eat = eat;
        this.toilet = toilet;
        this.bath = bath;
        this.walk = walk;
        this.story = story;
    }

    // 로그인
    @Builder(builderMethodName = "signupBuilder")
    public User(String username, UserType userType, String role) {
        this.username = username;
        this.userType = userType;
        this.role = role;
    }

    // 로그인 전
    @Builder(builderMethodName = "preSignupBuilder")
    public User(String kakaoId, String role) {
        this.kakaoId = kakaoId;
        this.role = role;
    }


    // 위치 인증 상태 업데이트
    public void updateUserLocationAuthentication(Boolean b) {
        this.locationAuthentication = b;
    }

    // 주소 업데이트
    public void updateUserAddress(String address, String detailAddress, String city) {
        this.address = address;
        this.detailAddress = detailAddress;
        this.locationAuthentication = true;
        this.city = city;
    }

    // 요약 업데이트
    public void updateAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    // 나의 이야기 업데이트
    public void updateStory(String story) {
        this.story = story;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id); // ID로 비교
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
