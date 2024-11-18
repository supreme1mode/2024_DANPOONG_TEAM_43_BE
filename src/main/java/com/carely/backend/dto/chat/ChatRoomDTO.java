package com.carely.backend.dto.chat;

import com.carely.backend.domain.ChatRoomEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoomDTO {
    private String roomId;
    private Long user1;
    private Long user2;
    private String lastMessage;
    private LocalDateTime lastUpdated;

    public ChatRoomDTO(com.carely.backend.domain.ChatRoomEntity chatRoomEntity) {
        this.roomId = chatRoomEntity.getRoomId();
        this.user1 = chatRoomEntity.getUser1();
        this.user2 = chatRoomEntity.getUser2();
        this.lastMessage = chatRoomEntity.getLastMessage();
        this.lastUpdated = chatRoomEntity.getLastUpdated();
    }
}