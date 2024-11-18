package com.carely.backend.domain;

import com.carely.backend.service.chat.ChatService;
import com.carely.backend.service.chat.SessionManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEntity {
    private static final Logger logger = LoggerFactory.getLogger(ChatRoomEntity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;
    private Long user1;
    private Long user2;

    @JsonIgnore
    @Builder.Default
    private Set<String> sessionIds = new HashSet<>();

    @Setter
    private String lastMessage; // 가장 최근에 보낸 메시지

    @Setter
    @Builder.Default
    private LocalDateTime lastUpdated = LocalDateTime.now(); // 가장 최근 메시지를 보낸 시간

    private boolean isBlocked; // 방 차단 상태

    // private boolean isActive; // 상담 활성화 상태

    public <T> void sendMessage(T message, ChatService chatService) {
        sessionIds.forEach(sessionId -> {
            WebSocketSession session = SessionManager.getSession(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    chatService.sendMessage(session, message);
                } catch (Exception e) {
                    logger.error("메시지 전송 중 오류 발생: {}", e.getMessage(), e);
                }
            }
        });
    }

//    public void updateIsActiveState(boolean b) {
//        this.isActive = b;
//    }
}
