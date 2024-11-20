package com.carely.backend.dto.guestBook;

import com.carely.backend.domain.GuestBookEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseGuestBookDTO {
    private Long sectionId;
    private String volunteerName;
    private Integer durationHours;
    private String caregiverName;
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
                .durationHours(guestBookEntity.getVolunteerSection().getDurationHours())
                .caregiverName(guestBookEntity.getCaregiver().getUsername())
                .caregiverAge(guestBookEntity.getCaregiver().getAge())
                .content(guestBookEntity.getContent())
                .build();
    }
}
