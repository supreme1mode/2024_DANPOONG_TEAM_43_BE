package com.carely.backend.dto.news;

import com.carely.backend.domain.News;
import com.carely.backend.domain.enums.UserType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class CreateNewsDTO {
    @NotNull
    private String title;

    @NotNull
    private String content;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        private Long newsId;
        private String title;
        private String content;
        private String writer;
        private LocalDateTime createdAt;

        public static Res toDTO(News news) {
            return Res.builder()
                    .writer(news.getWriter().getUsername())
                    .title(news.getTitle())
                    .content(news.getContent())
                    .createdAt(news.getCreatedAt())
                    .build();
        }

    }
}
