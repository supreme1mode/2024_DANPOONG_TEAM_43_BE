package com.carely.backend.controller;


import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.VolunteerAPI;
import com.carely.backend.domain.ChatMessageEntity;
import com.carely.backend.domain.ChatRoomEntity;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.volunteer.CreateVolunteerDTO;
import com.carely.backend.dto.volunteer.GetVolunteerInfoDTO;
import com.carely.backend.dto.volunteer.UpdateVolunteerApprovalDTO;
import com.carely.backend.service.UserService;
import com.carely.backend.service.VolunteerService;
import com.carely.backend.service.chat.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 자원봉사 요청을 생성한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_CREATE_VOLUNTEER\", \"message\": \"자원봉사 요청을 성공적으로 생성했습니다.\", \"data\": { \"id\": 3, \"volunteerId\": 51, \"caregiverId\": 50, \"startTime\": \"2024-11-17T04:33:06.36\", \"endTime\": \"2024-11-17T04:33:06.36\", \"durationHours\": 4, \"salary\": 0, \"location\": \"경기도 용인시 수지구 어딘가\", \"mainTask\": \"설거지\", \"volunteerType\": \"VOLUNTEER_REQUEST\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\" } }"))),

            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터로 자원봉사 요청 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 400, \"code\": \"INVALID_REQUEST\", \"message\": \"요청 데이터가 잘못되었습니다.\", \"data\": null }"))),

            @ApiResponse(responseCode = "404", description = "해당 채팅방이나 사용자를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"NOT_FOUND\", \"message\": \"해당 채팅방 또는 사용자를 찾을 수 없습니다.\", \"data\": null }")))
    })
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
                .timestamp(LocalDateTime.now().plusHours(9))
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

        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        CreateVolunteerDTO.Res res = volunteerService.updateApproval(volunteerId, updateVolunteerApprovalDTO.getMessageId(), updateVolunteerApprovalDTO.getRoomId(), kakaoId);

        //ChatRoomEntity chatRoom = chatService.findRoomById(updateVolunteerApprovalDTO.getRoomId());

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .type(ChatMessageEntity.MessageType.RESERVATION)
                .roomId(updateVolunteerApprovalDTO.getRoomId())
                .senderId(res.getCaregiverId())
                .message("약속을 수락했어요!")
                .timestamp(LocalDateTime.now().plusHours(9))
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
