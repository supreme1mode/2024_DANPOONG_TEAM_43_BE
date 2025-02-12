package com.carely.backend.dto.certificate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class volunteerDTO {
    private String volunteerSessionId;
    private String identity;
    private String username;
    private Integer volunteerHours;
    private String date;
    private String volunteerType;

}
