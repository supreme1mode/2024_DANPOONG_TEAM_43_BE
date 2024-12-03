package com.carely.backend.dto.document;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDocumentDTO {
    private String myType;
    private String myName;
    private String volunteerSessionType;
    private String partnerType;
    private String partnerName;
    private String volunteerDate;
    private String myIdentity;
    private String address;
    private int durationTimes;
    private String content;
}
