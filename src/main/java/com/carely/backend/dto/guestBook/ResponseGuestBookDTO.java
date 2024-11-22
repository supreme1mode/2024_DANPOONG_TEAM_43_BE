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
    private String home;
    private String writer;
    private String profileName;
    private UserType userType;
    //private String volunteerName;
    //private UserType volunteerUserType;
    private Integer durationHours;
    //private String caregiverName;
    //private UserType caregiverUserType;
    //private String caregiverAddress;
    //private Integer caregiverAge;
    private String careDate;
    private String content;

    public static ResponseGuestBookDTO entityToDto(GuestBookEntity guestBookEntity, String home, String writer, String profileName, UserType userType) {
        return ResponseGuestBookDTO.builder()
                .sectionId(guestBookEntity.getVolunteerSection().getId())
                .home(home)
                .writer(writer)
                .profileName(profileName)
                .userType(userType)
                .careDate(guestBookEntity.getVolunteerSection().getEndTime().toString())
                .durationHours(guestBookEntity.getVolunteerSection().getDurationHours())
                .content(guestBookEntity.getContent())
                .build();
    }
}
