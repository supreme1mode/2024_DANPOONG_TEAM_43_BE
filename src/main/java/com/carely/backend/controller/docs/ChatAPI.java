package com.carely.backend.controller.docs;

import com.carely.backend.domain.ChatMessageEntity;
import com.carely.backend.dto.chat.ChatRequest;
import com.carely.backend.dto.chat.ChatRoomResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatAPI {
    @Operation(summary = "채팅방을 생성하기", description = "채팅방을 생성합니다.")
    public ResponseEntity<?> createRoom(@RequestBody ChatRequest chatRequest);

    @Operation(summary = "메세지 전송하기", description = "메세지를 전송합니다.")
    public ChatMessageEntity sendMessage(ChatMessageEntity chatMessage);

//    @Operation(summary = "채팅을 진행하고 있는 모든 채팅방 조회하기", description = "채팅을 진행하고 있는 모든 채팅방을 조회합니다.")
//    public ResponseEntity<ResponseDTO<List<ChatRoomResponseDTO>>> findAllChatRooms() ;

    @Operation(summary = "채팅방에 해당하는 메세지 조회하기.", description = "채팅방에 해당하는 메세지들 조회합니다.")
    public ResponseEntity<?> getMessagesByRoomId(@PathVariable("roomId") String roomId);

    @Operation(summary = "채팅방 종료하기(웹소켓 close)", description = "채팅방을 종료합니다. 채팅 내역이 더 이상 조회되지 않습니다.")
    public ResponseEntity<String> closeRoom(@PathVariable("roomId") String roomId);

    @Operation(summary = "채팅방 재시작하기(웹소켓 open)", description = "채팅방을 재시작합니다. 채팅 내역이 조회됩니다.")
    public ResponseEntity<String> openRoom(@PathVariable("roomId") String roomId) ;

    @Operation(summary = "채팅방에 이미지 전송하기", description = "채팅방에 이미지를 전송합니다.")
    public ResponseEntity<ChatMessageEntity> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("roomId") String roomId);

    @Operation(summary = "열린 채팅방 조회하기", description = "열린 채팅방을 조회합니다.")
    public ResponseEntity<ResponseDTO<List<ChatRoomResponseDTO>>> findAllChatRooms();
}
