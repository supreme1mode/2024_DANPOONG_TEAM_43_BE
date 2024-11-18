package com.carely.backend.service.chat;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.carely.backend.domain.ChatMessageEntity;
import com.carely.backend.domain.ChatRoomCountEntity;
import com.carely.backend.domain.ChatRoomEntity;
import com.carely.backend.domain.User;
import com.carely.backend.dto.chat.ChatRoomResponseDTO;
import com.carely.backend.repository.ChatMessageRepository;
import com.carely.backend.repository.ChatRoomCountRepository;
import com.carely.backend.repository.ChatRoomRepository;
import com.carely.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomCountRepository chatRoomCountRepository;
    private final UserRepository userRepository;
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<ChatRoomResponseDTO> findAllRoom(String kakaoId) {
        User viewer = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        System.out.println("get viewer");
        // 채팅방 조회
        List<ChatRoomEntity> rooms = chatRoomRepository.findByUser1OrUser2(viewer.getId());

        List<ChatRoomResponseDTO> roomResponseDTOS = new ArrayList<>(); // 결과를 저장할 리스트

        for (ChatRoomEntity room : rooms) {
            if (room == null) {
                throw new IllegalArgumentException("Room cannot be null");
            }

            User receiver = null;
            Long user1 = room.getUser1();
            Long user2 = room.getUser2();

            if (viewer.getId() == room.getUser2()) {
                receiver = userRepository.findById(room.getUser1())
                        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. user1: " + room.getUser1()));
            } else {
                receiver = userRepository.findById(room.getUser2())
                        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. user2: " + room.getUser2()));
            }

            if (receiver == null) {
                throw new IllegalStateException("Receiver cannot be null");
            }

            // DTO 변환
            ChatRoomResponseDTO dto = ChatRoomResponseDTO.toDTO(room, receiver);
            System.out.println("dto: " + dto);

            roomResponseDTOS.add(dto); // 리스트에 추가
        }

        System.out.println(roomResponseDTOS);

        return roomResponseDTOS;
    }

    public ChatRoomEntity findRoomById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId).orElse(null);
    }

    public ChatRoomEntity findRoomByUserName(String kakaoId, Long reciverId) {
        User sender = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));


        return chatRoomRepository.findByUsers(sender.getId(), reciverId).orElse(null);
    }

    public ChatRoomEntity createRoom(String kakaoId, Long reciverId) {
        User sender = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Optional<ChatRoomEntity> existingRoom = chatRoomRepository.findByUsers(sender.getId(), reciverId);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        ChatRoomCountEntity chatRoomCount = chatRoomCountRepository.findById(1L).orElse(null);
        if (chatRoomCount == null) {
            chatRoomCount = new ChatRoomCountEntity();
            chatRoomCount.setTotalCount(1);
        } else {
            chatRoomCount.setTotalCount(chatRoomCount.getTotalCount() + 1);
        }

        // 새로운 채팅방 생성
        String randomId = UUID.randomUUID().toString();
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .roomId(randomId)
                .user1(sender.getId())
                .user2(reciverId)
                .build();

        chatRoomCountRepository.save(chatRoomCount);
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatMessageEntity saveChatMessage(ChatMessageEntity message) {
        ChatMessageEntity savedMessage = chatMessageRepository.save(message);

        Optional<ChatRoomEntity> chatRoomOptional = chatRoomRepository.findByRoomId(message.getRoomId());
        chatRoomOptional.ifPresent(chatRoom -> {
            if (message.getType() == ChatMessageEntity.MessageType.RESERVATION)
                chatRoom.setLastMessage("약속 요청이 도착했어요!");
            else
                chatRoom.setLastMessage(message.getMessage());
            // 한국 시간
            ZonedDateTime nowInSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
            chatRoom.setLastUpdated(nowInSeoul.toLocalDateTime());
            chatRoomRepository.save(chatRoom);
        });
        return savedMessage;
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                log.error("Error sending message: {}", e.getMessage(), e);
            }
        } else {
            log.warn("Session is null or closed, cannot send message.");
        }
    }

    public List<ChatMessageEntity> findMessagesByRoomId(String roomId) {
        log.info("Finding messages for roomId: {}", roomId);
        return chatMessageRepository.findByRoomId(roomId);
    }

    // 상담 종료 + 차단
    public void closeRoom(String roomId) {
        ChatRoomEntity room = findRoomById(roomId);
        if (room != null) {
            // room.updateIsActiveState(false);
            room.setBlocked(true);
            chatRoomRepository.save(room);
        }
    }

    // 상담 재개 + 활성화
    public void openRoom(String roomId) {
        ChatRoomEntity room = findRoomById(roomId);
        if (room != null) {
            // room.updateIsActiveState(true);
            room.setBlocked(false);
            chatRoomRepository.save(room);
        }
    }


    // 파일 업로드
    @Transactional
    public ChatMessageEntity uploadFile(byte[] fileData, String fileName, String roomId, String kakaoId) {
        User sender = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));


        String uniqueFileName = UUID.randomUUID().toString() + "-" + fileName;
        try {
            // S3에 파일 업로드
            s3Client.putObject(new PutObjectRequest(bucketName, uniqueFileName, new ByteArrayInputStream(fileData), null));
            String fileUrl = s3Client.getUrl(bucketName, uniqueFileName).toString();

            // 한국 시간 설정
            LocalDateTime timestamp = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();

            ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                    .fileUrl(fileUrl)
                    .fileName(fileName)
                    .roomId(roomId)
                    .senderId(sender.getId())
                    .type(ChatMessageEntity.MessageType.FILE)
                    .timestamp(timestamp)
                    .message("사진을 보냈습니다.")
                    .build();

            return saveChatMessage(chatMessage);
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}
