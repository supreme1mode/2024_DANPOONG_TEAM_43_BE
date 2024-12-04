package com.carely.backend.dto.easyCodef;

import lombok.Getter;

@Getter
public class RequestUserIdentityDTO {
    //핸드폰 번호
    private String phoneNum;
    //이름
    private String username;
    //발급일자
    private String issueDate;
    //주민번호
    private String identity;
}