package com.carely.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotUserDTO {
    private String kakaoId;
    private String nickname;
    private String gender;
    private String birthyear;
    private String birthmonth;
    private String birthday;
    private String phoneNum;
}
