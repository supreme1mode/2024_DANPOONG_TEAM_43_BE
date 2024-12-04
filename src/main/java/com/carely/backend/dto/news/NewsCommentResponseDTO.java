package com.carely.backend.dto.news;

import com.carely.backend.domain.NewsComment;
import com.carely.backend.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NewsCommentResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class List {
        private Long newsCommentId;
        private String content;
        private UserType writerType;
        private String writer;
        private Long writerId;
        private LocalDateTime createdAt;

        public static List toDTO (NewsComment e) {
            return List.builder()
                    .newsCommentId(e.getId())
                    .content(e.getContent())
                    .writer(e.getWriter().getUsername())
                    .writerType(e.getWriter().getUserType())
                    .createdAt(e.getCreatedAt())
                    .writerId(e.getWriter().getId())
                    .build();
        }
    }
}
