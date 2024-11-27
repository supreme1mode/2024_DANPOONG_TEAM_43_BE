package com.carely.backend.controller;


import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.VolunteerAPI;
import com.carely.backend.domain.ChatMessageEntity;
import com.carely.backend.domain.ChatRoomEntity;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.volunteer.CreateVolunteerDTO;
import com.carely.backend.dto.volunteer.GetVolunteerInfoDTO;
import com.carely.backend.dto.volunteer.UpdateVolunteerApprovalDTO;
import com.carely.backend.service.UserService;
import com.carely.backend.service.VolunteerService;
import com.carely.backend.service.chat.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/volunteer")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")  // Allow only from localhost:3000
public class VolunteerController implements VolunteerAPI {
    private final VolunteerService volunteerService;
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<?> createVolunteer(@RequestBody CreateVolunteerDTO dto) {
        CreateVolunteerDTO.Res res = volunteerService.createVolunteer(dto);

        String jsonMessage = convertToJson(res);
        ChatRoomEntity chatRoom = chatService.findRoomById(dto.getRoomId());

        // 채팅 생성
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .type(ChatMessageEntity.MessageType.RESERVATION)
                .roomId(dto.getRoomId())
                .senderId(res.getVolunteerId())
                .message(jsonMessage)
                .timestamp(LocalDateTime.now())
                .isApproved(false)
                .type(ChatMessageEntity.MessageType.RESERVATION)
                .build();


        if (chatRoom != null) {
            if (chatRoom.isBlocked()) {
                throw new IllegalArgumentException("This room is blocked.");
            }
            chatRoom.sendMessage(chatMessage, chatService);

            chatMessage.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime());
            chatService.saveChatMessage(chatMessage);

            messagingTemplate.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
        }

        return ResponseEntity
                .status(SuccessCode.SUCCESS_CREATE_VOLUNTEER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_VOLUNTEER, res));
    }

    @PatchMapping("/approval/{volunteerId}")
    public ResponseEntity<?> approveVolunteer(@PathVariable("volunteerId") Long volunteerId,
                                             @RequestBody() UpdateVolunteerApprovalDTO updateVolunteerApprovalDTO) throws Exception {

        //String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        CreateVolunteerDTO.Res res = volunteerService.updateApproval(volunteerId, updateVolunteerApprovalDTO.getMessageId(), updateVolunteerApprovalDTO.getRoomId(), "3777478397");

        //ChatRoomEntity chatRoom = chatService.findRoomById(updateVolunteerApprovalDTO.getRoomId());

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .type(ChatMessageEntity.MessageType.RESERVATION)
                .roomId(updateVolunteerApprovalDTO.getRoomId())
                .senderId(res.getCaregiverId())
                .message("약속을 수락했어요!")
                .timestamp(LocalDateTime.now())
                .isApproved(true)
                .build();

//        if (chatRoom != null) {
//            if (chatRoom.isBlocked()) {
//                throw new IllegalArgumentException("This room is blocked.");
//            }
            // 변경된 메시지를 저장 -> 웹소켓으로 브로드캐스트
            chatMessage = chatService.saveChatMessage(chatMessage);
            messagingTemplate.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
        //}

        return ResponseEntity
                .status(SuccessCode.SUCCESS_APPROVAL.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_APPROVAL, res));
    }

    // 봉사 조회
    @GetMapping("/{volunteerId}")
    public ResponseEntity<?> getVolunteerInfo(@PathVariable("volunteerId") Long volunteerId) {
        GetVolunteerInfoDTO res = volunteerService.getVolunteerInfo(volunteerId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_VOLUNTEER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_VOLUNTEER, res));
    }

    public String convertToJson(CreateVolunteerDTO.Res res) {
        try {
            return objectMapper.writeValueAsString(res);
        } catch (Exception e) {
            throw new RuntimeException("Json 변환에 실패했습니다.", e);
        }
    }
}
