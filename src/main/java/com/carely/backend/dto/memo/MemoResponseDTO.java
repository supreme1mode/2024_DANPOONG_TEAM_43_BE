package com.carely.backend.dto.memo;

import com.carely.backend.domain.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemoResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class List {
        private Long memoId;
        private String content; // 메모 내용
        private String writerName; // 메모 작성자
        private String all;
        private String healthy;
        private String eat;
        private String additionalHealth;
        private String social;
        private String voiding;

        public static List toDTO(Memo e) {
            return List.builder()
                    .memoId(e.getId())
                    .content(e.getContent())
                    .writerName(e.getWriter().getUsername())
                    .all(e.getAll())
                    .healthy(e.getHealthy())
                    .eat(e.getEat())
                    .additionalHealth(e.getAdditionalHealth())
                    .social(e.getSocial())
                    .voiding(e.getVoiding())
                    .build();
        }
    }
}
