package com.carely.backend.dto.news;

import com.carely.backend.domain.News;
import com.carely.backend.domain.NewsComment;
import com.carely.backend.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NewsResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class List {
        private Long newsId;
        private String title;
        private String content;
        private UserType writerType;
        private String writer;
        private Integer commentCount;
        private LocalDateTime createdAt;

        public static List toDTO(News news) {
            Integer commentCount = news.getNewsComments().size();

            return List.builder()
                    .newsId(news.getId())
                    .createdAt(news.getCreatedAt())
                    .title(news.getTitle())
                    .content(news.getContent())
                    .writer(news.getWriter().getUsername())
                    .writerType(news.getWriter().getUserType())
                    .commentCount(commentCount)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail {
        private Long newsId;
        private String title;
        private String content;
        private UserType writerType;
        private String writer;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private java.util.List<NewsCommentResponseDTO.List> newsComments;

        public static Detail toDTO(News news) {
            Set<NewsComment> commentList = news.getNewsComments();

            java.util.List<NewsCommentResponseDTO.List> newsComments = commentList.stream()
                    .map(NewsCommentResponseDTO.List::toDTO)
                    .toList();


            return Detail.builder()
                    .newsId(news.getId())
                    .createdAt(news.getCreatedAt())
                    .title(news.getTitle())
                    .content(news.getContent())
                    .writer(news.getWriter().getUsername())
                    .writerType(news.getWriter().getUserType())
                    .newsComments(newsComments)
                    .build();
        }
    }
}
