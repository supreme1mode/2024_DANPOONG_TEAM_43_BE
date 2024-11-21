package com.carely.backend.domain;

import com.carely.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestBookEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 자원봉사 섹션
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "volunteer_section_id")
    private GuestBookEntity guestBook;

    @Lob
    private String content;

    // 자원봉사자
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "volunteer_id")
    private User volunteer;

    // 간병인
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "caregiver_id")
    private User caregiver;
}
