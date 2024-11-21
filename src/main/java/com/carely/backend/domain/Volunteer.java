package com.carely.backend.domain;

import com.carely.backend.domain.common.BaseEntity;
import com.carely.backend.domain.enums.VolunteerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Volunteer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 채팅방 id
    private String roomId;

    // 요청 날짜
    private LocalDate date;

    // 시작 시간
    private LocalDateTime startTime;

    // 종료 시간
    private LocalDateTime endTime;

    // 지속 시간(시간 단위)
    private Integer durationHours;

    // 급료
    private Integer salary;

    // 주된 일
    private String mainTask;

    // 타입 - 누가 요청하는 건지
    private VolunteerType volunteerType;

    // 만나는 위치
    private String location;

    // 자원봉사자 or 요양보호사
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "volunteer_id")
    private User volunteer;

    // 승인 상태
    private Boolean isApproved;

    @Builder.Default
    private Boolean hasGuestBook = Boolean.FALSE;

    // 간병인
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "caregiver_id")
    private User caregiver;

    // 승인 처리
    public void updateVolunteerApproval() {
        this.isApproved = true;
    }

    public void updateVolunteerGuestBook() {this.hasGuestBook = true;}
    public void deleteVolunteerGuestBook() {this.hasGuestBook = false;}
}
