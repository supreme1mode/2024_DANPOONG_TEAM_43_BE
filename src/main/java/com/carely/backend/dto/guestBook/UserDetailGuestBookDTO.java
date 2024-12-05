package com.carely.backend.dto.guestBook;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailGuestBookDTO {
    private String partnerUsername;
    private Long partnerUserId;
    private String partnerUserType;
    private String content;
    private String date;
}
