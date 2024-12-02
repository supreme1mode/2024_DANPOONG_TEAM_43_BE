package com.carely.backend.dto.easyCodef;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserIdentityDTO {
    private String organization;
    private String loginType;
    private String loginTypeLevel;
    private String telecom;
    private String phoneNo;
    private String loginUserName;
    private String loginIdentity;
    private String loginBirthDate;
    private String birthDate;
    private String identity;
    private String userName;
    private String issueDate;
    private String identityEncYn;
}
