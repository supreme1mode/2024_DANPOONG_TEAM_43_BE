package _oorm.caregiver.backend.dto.chat;

import _oorm.caregiver.backend.domain.chat.ChatRoomEntity;
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

    public ChatRoomDTO(ChatRoomEntity chatRoomEntity) {
        this.roomId = chatRoomEntity.getRoomId();
        this.user1 = chatRoomEntity.getUser1();
        this.user2 = chatRoomEntity.getUser2();
        this.lastMessage = chatRoomEntity.getLastMessage();
        this.lastUpdated = chatRoomEntity.getLastUpdated();
    }
}