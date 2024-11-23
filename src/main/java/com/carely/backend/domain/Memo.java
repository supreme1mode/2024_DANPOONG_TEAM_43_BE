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
public class Memo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content; // 메모 내용

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private User writer; // 메모 작성자

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver; // 대상자

    @Column(columnDefinition = "TEXT", name = "all_")
    private String all;
    @Column(columnDefinition = "TEXT")
    private String healthy;
    @Column(columnDefinition = "TEXT")
    private String eat;
    @Column(columnDefinition = "TEXT")
    private String additionalHealth;
    @Column(columnDefinition = "TEXT")
    private String social;
    @Column(columnDefinition = "TEXT")
    private String voiding;
}
