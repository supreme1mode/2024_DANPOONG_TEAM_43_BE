package com.carely.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 255)
    private MessageType type;
    private String roomId;
    private Long senderId;
    @Column(columnDefinition = "TEXT")
    private String message;
    private LocalDateTime timestamp;

    private String fileUrl;
    private String fileName;
    private Boolean isApproved = false;

    public void updateVolunteerApproval() {
        this.isApproved = true;
    }

    public enum MessageType {
        ENTER, TALK, FILE, RESERVATION
    }
}