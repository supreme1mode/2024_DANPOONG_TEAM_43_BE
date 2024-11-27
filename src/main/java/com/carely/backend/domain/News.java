package com.carely.backend.domain;

import com.carely.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class News extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private User writer; // 소식 작성자

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group; // 작성한 그룹

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NewsComment> newsComments = new HashSet<>(); // 댓글

    private String title;
    private String content;

}
