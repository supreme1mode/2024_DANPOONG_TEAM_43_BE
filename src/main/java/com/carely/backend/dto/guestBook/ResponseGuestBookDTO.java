package com.carely.backend.dto.guestBook;

import com.carely.backend.domain.GuestBookEntity;
import com.carely.backend.domain.enums.UserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseGuestBookDTO {
    private Long sectionId;
    private String volunteerName;
    private UserType volunteerUserType;
    private Integer durationHours;
    private String caregiverName;
    private UserType caregiverUserType;
    private String caregiverAddress;
    private Integer caregiverAge;
    private String careDate;
    private String content;

    public static ResponseGuestBookDTO entityToDto(GuestBookEntity guestBookEntity) {
        return ResponseGuestBookDTO.builder()
                .sectionId(guestBookEntity.getVolunteerSection().getId())
                .careDate(guestBookEntity.getVolunteerSection().getEndTime().toString())
                .caregiverAddress(guestBookEntity.getCaregiver().getAddress())
                .volunteerName(guestBookEntity.getVolunteer().getUsername())
                .volunteerUserType(guestBookEntity.getVolunteer().getUserType())
                .durationHours(guestBookEntity.getVolunteerSection().getDurationHours())
                .caregiverName(guestBookEntity.getCaregiver().getUsername())
                .caregiverUserType(guestBookEntity.getCaregiver().getUserType())
                .caregiverAge(guestBookEntity.getCaregiver().getAge())
                .content(guestBookEntity.getContent())
                .build();
    }
}
