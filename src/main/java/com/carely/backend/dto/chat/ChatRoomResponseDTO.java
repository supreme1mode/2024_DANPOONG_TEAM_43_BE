package _oorm.caregiver.backend.dto.chat;

import _oorm.caregiver.backend.domain.SuperUser;
import _oorm.caregiver.backend.domain.chat.ChatRoomEntity;
import _oorm.caregiver.backend.domain.enums.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    public static ChatRoomResponseDTO toDTO(ChatRoomEntity room, SuperUser user) {
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
