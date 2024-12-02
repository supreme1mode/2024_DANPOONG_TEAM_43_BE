package com.carely.backend.dto.easyCodef;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({"simpleAuth", "is2Way", "twoWayInfo"}) // 필드 순서 지정
public class AdditionalAuthDTO {
    private String organization;
    private String identity;
    private String userName;
    private String issueDate;
    @JsonProperty("simpleAuth")
    private String simpleAuth;         // 간편 인증 상태

    @JsonProperty("is2Way")
    private boolean is2Way;            // 추가 인증 여부

    @JsonProperty("twoWayInfo")
    private TwoWayInfoDTO twoWayInfo;  // 추가 인증 정보

    @Getter
    @Builder
    public static class TwoWayInfoDTO {
        @JsonProperty("jobIndex")
        private int jobIndex;

        @JsonProperty("threadIndex")
        private int threadIndex;

        @JsonProperty("jti")
        private String jti;

        @JsonProperty("twoWayTimestamp")
        private long twoWayTimestamp;
    }

}
