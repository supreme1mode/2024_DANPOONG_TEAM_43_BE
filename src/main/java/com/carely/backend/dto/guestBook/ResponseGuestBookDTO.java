package com.carely.backend.dto.guestBook;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseGuestBookDTO {
    private GuestBookDTO otherType; // 남의 거
    private GuestBookDTO caregiver; // 내 거

    @Getter
    @Builder
    public static class GuestBookDTO {
        private String userType;
        private String username;
        private String content;
        private Long userId;
    }
}
