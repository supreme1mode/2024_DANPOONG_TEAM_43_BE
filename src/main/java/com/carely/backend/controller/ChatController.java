package com.carely.backend.controller;

import com.carely.backend.code.ErrorCode;
import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.ChatAPI;
import com.carely.backend.domain.ChatMessageEntity;
import com.carely.backend.domain.ChatRoomEntity;
import com.carely.backend.dto.chat.ChatRequest;
import com.carely.backend.dto.chat.ChatRoomResponseDTO;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import com.carely.backend.service.UserService;
import com.carely.backend.service.chat.ChatService;
import com.carely.backend.service.s3.S3Uploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController implements ChatAPI {

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    private S3Uploader s3Uploader;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    // 채팅방 만들기
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody ChatRequest chatRequest) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long reciverId = chatRequest.getToUser();
        // 사용자 인증
        if (kakaoId == null || kakaoId.equals("anonymousUser")) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorCode.BAD_REQUEST);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);
        }

        ChatRoomEntity existingRoom = chatService.findRoomByUserName(kakaoId, reciverId);
        if (existingRoom != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDTO<>(SuccessCode.SUCCESS_EXIST_CHATROOM, existingRoom));
        }

        // 새로운 채팅방을 생성
        ChatRoomEntity newRoom = chatService.createRoom(kakaoId, reciverId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_CHATROOM, newRoom));
    }

    // 모든 유저 조회
//    @GetMapping("/users")
//    public ResponseEntity<ResponseDTO<List<UserResponseDTO>>> findAllUsers() {
//        List<UserResponseDTO> users = userService.findAllUsers();
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_ALL_USERS, users));
//    }

    // 메시지 보내기
    @MessageMapping("/chat.sendMessage")
    public ChatMessageEntity sendMessage(ChatMessageEntity chatMessage) {
        // System.out.println(chatMessage.getSenderId());
        log.info("Received message to send: {}", chatMessage.getSenderId());
        // String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        ChatRoomEntity chatRoom = chatService.findRoomById(chatMessage.getRoomId());
        if (chatRoom != null) {
            if (chatRoom.isBlocked()) {
                chatRoom.setBlocked(false);
                //throw new IllegalArgumentException("This room is blocked.");
            }
            chatRoom.sendMessage(chatMessage, chatService);

            chatMessage.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime());
            chatService.saveChatMessage(chatMessage);

            messagingTemplate.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
        }
        return chatMessage;
    }

    // 모든 채팅방 조회
//    @GetMapping("/rooms")
//    public ResponseEntity<ResponseDTO> findAllChatRooms() {
//        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        List<ChatRoomResponseDTO> rooms = chatService.findAllRoom(kakaoId);
//
//        return ResponseEntity
//                .status(SuccessCode.SUCCESS_FIND_CHATROOM.getStatus().value())
//                .body(new ResponseDTO<>(SuccessCode.SUCCESS_FIND_CHATROOM, rooms));
//    }

    @GetMapping("/rooms")
    public ResponseEntity<ResponseDTO<List<ChatRoomResponseDTO>>> findAllChatRooms(@AuthenticationPrincipal CustomUserDetails user) {
        List<ChatRoomResponseDTO> rooms = chatService.findAllRoom(user.getUsername());

        System.out.println("get rooms");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_FIND_CHATROOM, rooms));
    }

    // 채팅 내역 조회
    @GetMapping("/rooms/messages/{roomId}")
    public ResponseEntity<?> getMessagesByRoomId(@PathVariable("roomId") String roomId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userService.getUserId(kakaoId);

        ChatRoomEntity room = chatService.findRoomById(roomId);
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "채팅방을 찾을 수 없습니다."));
        }

        // 로그인된 사용자의 username = 채팅방 name => 채팅방 blocked 상태여도 메시지 조회 허용
        if (room.isBlocked() && (!(room.getUser1() == userId) || !(room.getUser2() == userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "채팅방 입장이 차단되었습니다."));
        }

        List<ChatMessageEntity> messages = chatService.findMessagesByRoomId(roomId);
//        if (messages.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("error", "채팅방에 메시지가 없습니다."));
//        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_FIND_CHATROOM, messages));

        // ret
    }

    // 방 종료 (상담 종료 + 방 차단)
    @PostMapping("/rooms/{roomId}/close")
    public ResponseEntity<String> closeRoom(@PathVariable("roomId") String roomId) {
        ChatRoomEntity room = chatService.findRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        chatService.closeRoom(roomId);
        return ResponseEntity.ok("상담이 종료되고, 채팅방이 차단 상태가 되었습니다.");
    }

    // 방 열기 (상담 시작 + 방 활성화)
    @PostMapping("/rooms/{roomId}/open")
    public ResponseEntity<String> openRoom(@PathVariable("roomId") String roomId) {
        ChatRoomEntity room = chatService.findRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        chatService.openRoom(roomId);
        return ResponseEntity.ok("상담이 재개되고, 채팅방이 활성화 상태가 되었습니다.");
    }

    //파일 업로드
    @PostMapping(value = "/upload/{roomId}",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatMessageEntity> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("roomId") String roomId) {
        try {
            // 현재 로그인된 사용자 이름 가져오기
            String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

            // 파일을 S3에 업로드
            String fileUrl = s3Uploader.upload(file, "chat-uploads");

            // ChatMessageEntity 생성
            ChatMessageEntity chatMessage = chatService.uploadFile(file.getBytes(), file.getOriginalFilename(), roomId, kakaoId);

            // 웹소켓
            messagingTemplate.convertAndSend("/topic/" + roomId, chatMessage);

            log.info("File uploaded successfully: {}", file.getOriginalFilename());

            return ResponseEntity.ok(chatMessage);
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
