package com.carely.backend.dto.news;

import com.carely.backend.domain.News;
import com.carely.backend.domain.NewsComment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class CreateCommentDTO {
    @NotNull
    private String content;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private Long commentId;
        private String content;
        private String writer;
        private LocalDateTime createdAt;

        public static CreateCommentDTO.Res toDTO(NewsComment comment) {
            return CreateCommentDTO.Res.builder()
                    .commentId(comment.getId())
                    .writer(comment.getWriter().getUsername())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }

    }
}
