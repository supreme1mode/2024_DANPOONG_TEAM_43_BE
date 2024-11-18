package com.carely.backend.dto.chat;


import com.carely.backend.domain.ChatRoomEntity;
import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDTO {
    private String roomId;
    private String receiverName;
    private Long receiverId;
    private UserType receiverUserType;
    private Long user1;
    private Long user2;
    private String lastMessage; // 가장 최근에 보낸 메시지
    private LocalDateTime lastUpdated = LocalDateTime.now(); // 가장 최근 메시지를 보낸 시간
    private boolean isBlocked; // 방 차단 상태

    public static ChatRoomResponseDTO toDTO(ChatRoomEntity room, User user) {
        return ChatRoomResponseDTO.builder()
                .roomId(room.getRoomId())
                .receiverId(user.getId())
                .receiverName(user.getUsername())
                .receiverUserType(user.getUserType())
                .user1(room.getUser1())
                .user2(room.getUser2())
                .lastMessage(room.getLastMessage())
                .lastUpdated(room.getLastUpdated())
                .isBlocked(room.isBlocked())
                .build();
    }
}
