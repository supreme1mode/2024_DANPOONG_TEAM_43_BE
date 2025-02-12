package com.carely.backend.dto.guestBook;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseGroupGuestbookDTO {
    private String volunteerSessionId;
    private GuestBookDTO otherType;
    private GuestBookDTO caregiver;

    @Getter
    @Builder
    public static class GuestBookDTO {
        private String userType;
        private String username;
        private String content;
        private Long userId;
    }
}
