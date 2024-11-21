package com.carely.backend.dto.guestBook;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestGuestBookDTO {
    //private Volunteer id; //Section id 약속에 대한
    private String content;
}
