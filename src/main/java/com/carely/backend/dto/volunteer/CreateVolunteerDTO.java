package com.carely.backend.dto.volunteer;


import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.VolunteerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class CreateVolunteerDTO {
    private Long volunteerId;
    private Long caregiverId;

    // 시작 시간
    private LocalDateTime startTime;

    // 종료 시간
    private LocalDateTime endTime;

    // 지속 시간(시간 단위)
    private Integer durationHours;

    // 만나는 위치
    private String location;

    // 급료
    private Integer salary;

    // 주된 일
    private String mainTask;

    // 타입 - 누가 요청하는 건지
    private VolunteerType volunteerType;

    // 방 번호
    private String roomId;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private Long id;
        private Long volunteerId;
        private Long caregiverId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer durationHours;
        private Integer salary;
        private String location;
        private String mainTask;
        // private Boolean isApproved;
        private VolunteerType volunteerType;
        private String roomId;

        public static Res toDTO(Volunteer e) {
            return Res.builder()
                    .id(e.getId())
                    .volunteerId(e.getVolunteer().getId())  // 자원봉사자 ID
                    .caregiverId(e.getCaregiver().getId())  // 간병인 ID
                    .startTime(e.getStartTime().plusHours(9))
                    .endTime(e.getEndTime().plusHours(9))
                    .durationHours(e.getDurationHours())
                    .salary(e.getSalary())
                    .mainTask(e.getMainTask())
                    .volunteerType(e.getVolunteerType())
                    .location(e.getLocation())
                    // .isApproved(e.getIsApproved())
                    .roomId(e.getRoomId())
                    .build();
        }
    }

}
