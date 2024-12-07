package com.carely.backend.dto.volunteer;


import com.carely.backend.domain.User;
import com.carely.backend.domain.Volunteer;
import com.carely.backend.domain.enums.VolunteerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetVolunteerInfoDTO {

    public static class Vol {
        private Long id;
        private Long volunteerId;
        private Long caregiverId;
        private String volunteerName;
        private Integer volunteerAge;
        private String phoneNum;
        private String address;

        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer durationHours;
        private Integer salary;
        private String location;
        private String mainTask;
        private VolunteerType volunteerType;
        private String roomId;

        public static GetVolunteerInfoDTO toDTO(Volunteer e, User volunteer) {
            return GetVolunteerInfoDTO.builder()
                    .id(e.getId())
                    .caregiverId(e.getCaregiver().getId())
                    .volunteerId(volunteer.getId())
                    .volunteerName(volunteer.getUsername())
                    .volunteerAge(volunteer.getAge())
                    .phoneNum(volunteer.getPhoneNum())
                    .address(volunteer.getAddress())
                    .startTime(e.getStartTime().plusHours(9))
                    .endTime(e.getEndTime().plusHours(9))
                    .durationHours(e.getDurationHours())
                    .salary(e.getSalary())
                    .location(e.getLocation())
                    .mainTask(e.getMainTask())
                    .volunteerType(e.getVolunteerType())
                    .roomId(e.getRoomId())
                    .build();
        }
    }
    private Long id;
    private Long volunteerId;
    private Long caregiverId;
    private String volunteerName;
    private String caregiverName;
    private Integer volunteerAge;
    private String phoneNum;
    private String address;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationHours;
    private Integer salary;
    private String location;
    private String mainTask;
    private VolunteerType volunteerType;
    private String roomId;
    private String memo;

    public static GetVolunteerInfoDTO toDTO(Volunteer e, User volunteer, User caregiver, String memo) {
        return GetVolunteerInfoDTO.builder()
                .id(e.getId())
                .caregiverId(e.getCaregiver().getId())
                .volunteerId(volunteer.getId())
                .volunteerName(volunteer.getUsername())
                .caregiverName(caregiver.getUsername())
                .volunteerAge(volunteer.getAge())
                .phoneNum(volunteer.getPhoneNum())
                .address(volunteer.getAddress())
                .startTime(e.getStartTime().plusHours(9))
                .endTime(e.getEndTime().plusHours(9))
                .durationHours(e.getDurationHours())
                .salary(e.getSalary())
                .location(e.getLocation())
                .mainTask(e.getMainTask())
                .volunteerType(e.getVolunteerType())
                .roomId(e.getRoomId())
                .memo(memo)
                .build();
    }
}
