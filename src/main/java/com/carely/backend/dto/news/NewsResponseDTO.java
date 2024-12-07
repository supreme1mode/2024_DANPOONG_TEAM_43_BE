package com.carely.backend.dto.news;

import com.carely.backend.domain.News;
import com.carely.backend.domain.NewsComment;
import com.carely.backend.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NewsResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class List implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L; // 직렬화 UID 추가
        private Long newsId;
        private String title;
        private String content;
        private UserType writerType;
        private String writer;
        private Long writerId;
        private Integer commentCount;
        private LocalDateTime createdAt;

        public static List toDTO(News news) {
            Integer commentCount = news.getNewsComments().size();

            return List.builder()
                    .newsId(news.getId())
                    .createdAt(news.getCreatedAt().plusHours(9))
                    .title(news.getTitle())
                    .content(news.getContent())
                    .writer(news.getWriter().getUsername())
                    .writerType(news.getWriter().getUserType())
                    .commentCount(commentCount)
                    .writerId(news.getWriter().getId())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail implements Serializable{
        @Serial
        private static final long serialVersionUID = 1L; // 직렬화 UID 추가
        private Long newsId;
        private String title;
        private String content;
        private UserType writerType;
        private String writer;
        private Long writerId;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private java.util.List<NewsCommentResponseDTO.List> newsComments;

        public static Detail toDTO(News news) {
            Set<NewsComment> commentList = news.getNewsComments();

            java.util.List<NewsCommentResponseDTO.List> newsComments = commentList.stream()
                    .map(NewsCommentResponseDTO.List::toDTO)
                    .sorted(Comparator.comparing(NewsCommentResponseDTO.List::getCreatedAt).reversed())  // 역순 정렬
                    .toList();


            return Detail.builder()
                    .newsId(news.getId())
                    .createdAt(news.getCreatedAt().plusHours(9))
                    .title(news.getTitle())
                    .content(news.getContent())
                    .writer(news.getWriter().getUsername())
                    .writerId(news.getWriter().getId())
                    .writerType(news.getWriter().getUserType())
                    .newsComments(newsComments)
                    .build();
        }
    }
}
