package com.carely.backend.domain;

import com.carely.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class NewsComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private User writer; // 소식 작성자

    private String content;

    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news; // 작성한 그룹
}
