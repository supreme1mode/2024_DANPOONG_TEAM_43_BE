package com.carely.backend.dto.memo;

import com.carely.backend.domain.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class CreateMemoDTO {
    private Long volunteerId;
    private String content; // 메모 내용

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private Long memoId;
        private String content; // 메모 내용
        private String writerName; // 메모 작성자
        private String receiverName; // 대상자
        private String aiSummary;

        public static Res toDTO(Memo e, String aiSummary) {

            return Res.builder()
                    .memoId(e.getId())
                    .content(e.getContent())
                    .writerName(e.getWriter().getUsername())
                    .receiverName(e.getReceiver().getUsername())
                    .aiSummary(aiSummary)
                    .build();
        }
    }
}
