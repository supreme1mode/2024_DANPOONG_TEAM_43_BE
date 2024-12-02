package com.carely.backend.dto.easyCodef;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Data
public class JuminResponseDTO {
    private Result result;
    private Data data;

    @Getter
    public static class Result {
        private String code;
        private String extraMessage;
        private String message;
        private String transactionId;
    }

    @Getter
    public static class Data {
        private int jobIndex;
        private int threadIndex;
        private String jti;
        private long twoWayTimestamp;

        @JsonProperty("continue2Way")
        private boolean continue2Way;

        private ExtraInfo extraInfo;
        private String method;

        @Getter
        public static class ExtraInfo {
            private String commSimpleAuth;
        }
    }
}
