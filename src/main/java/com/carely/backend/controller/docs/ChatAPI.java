package com.carely.backend.controller.docs;

import com.carely.backend.domain.ChatMessageEntity;
import com.carely.backend.dto.chat.ChatRequest;
import com.carely.backend.dto.chat.ChatRoomResponseDTO;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatAPI {
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "채팅방 생성에 성공한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 201, \"code\": \"SUCCESS_CREATE_CHATROOM\", \"message\": \"채팅방이 성공적으로 생성되었습니다.\", \"data\": { \"id\": 14, \"roomId\": \"ac035619-3602-40e8-8b5f-e7ee68c368a2\", \"user1\": 1, \"user2\": 4, \"lastMessage\": null, \"lastUpdated\": \"2024-11-10T07:10:32.2675425\", \"blocked\": false } }"))),

            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터로 채팅방 생성 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 400, \"code\": \"INVALID_REQUEST\", \"message\": \"요청 데이터가 잘못되었습니다.\", \"data\": null }"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = """
                                        {
                                            "status": 401,
                                            "code": "INVALID_ACCESS_TOKEN",
                                            "message": "유효하지 않은 토큰입니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_EXPIRED",
                                            "message": "토큰이 만료되었습니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_MISSING", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_MISSING",
                                            "message": "요청 헤더에 토큰이 없습니다.",
                                            "data": null
                                        }
                                        """)
                            }))
    })
    @Operation(summary = "채팅방을 생성하기", description = "채팅방을 생성합니다.")
    public ResponseEntity<?> createRoom(@RequestBody ChatRequest chatRequest);

    @Operation(summary = "메세지 전송하기", description = "메세지를 전송합니다.")
    ChatMessageEntity sendMessage(ChatMessageEntity chatMessage);

//    @Operation(summary = "채팅을 진행하고 있는 모든 채팅방 조회하기", description = "채팅을 진행하고 있는 모든 채팅방을 조회합니다.")
//    public ResponseEntity<ResponseDTO<List<ChatRoomResponseDTO>>> findAllChatRooms() ;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 채팅방 메시지를 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_FIND_CHATROOM\", \"message\": \"모든 채팅방을 조회했습니다.\", \"data\": [ { \"id\": 6, \"type\": \"TALK\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\", \"senderId\": 51, \"message\": \"반가워요\", \"timestamp\": \"2024-11-17T18:15:39.538526\", \"fileUrl\": null, \"fileName\": null, \"isApproved\": false }, { \"id\": 7, \"type\": \"TALK\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\", \"senderId\": 51, \"message\": \"ㅋㅎ\", \"timestamp\": \"2024-11-17T18:15:55.295389\", \"fileUrl\": null, \"fileName\": null, \"isApproved\": false }, { \"id\": 8, \"type\": \"RESERVATION\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\", \"senderId\": 51, \"message\": \"{\\\"id\\\":3,\\\"volunteerId\\\":51,\\\"caregiverId\\\":50,\\\"startTime\\\":\\\"2024-11-17T04:33:06.36\\\",\\\"endTime\\\":\\\"2024-11-17T04:33:06.36\\\",\\\"durationHours\\\":4,\\\"salary\\\":0,\\\"location\\\":\\\"경기도 용인시 수지구 어딘가\\\",\\\"mainTask\\\":\\\"설거지\\\",\\\"volunteerType\\\":\\\"VOLUNTEER_REQUEST\\\",\\\"roomId\\\":\\\"c0babffb-a39b-45e5-b326-a964b49e9409\\\"}\", \"timestamp\": \"2024-11-17T18:30:44.223335\", \"fileUrl\": null, \"fileName\": null, \"isApproved\": true }, { \"id\": 9, \"type\": \"RESERVATION\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\", \"senderId\": 50, \"message\": \"약속을 수락했어요!\", \"timestamp\": \"2024-11-17T18:19:04.822753\", \"fileUrl\": null, \"fileName\": null, \"isApproved\": true }, { \"id\": 23, \"type\": \"FILE\", \"roomId\": \"95c506c0-28f7-4877-80ad-6863ce2e1af9\", \"senderId\": 51, \"message\": \"사진을 보냈습니다.\", \"timestamp\": \"2024-11-17T22:03:20.713524\", \"fileUrl\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/cbf9f1dc-ff59-41e9-b612-e121eddab16c-KakaoTalk_20241114_101142529.jpg\", \"fileName\": \"KakaoTalk_20241114_101142529.jpg\", \"isApproved\": null } ] }"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = """
                                        {
                                            "status": 401,
                                            "code": "INVALID_ACCESS_TOKEN",
                                            "message": "유효하지 않은 토큰입니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_EXPIRED",
                                            "message": "토큰이 만료되었습니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_MISSING", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_MISSING",
                                            "message": "요청 헤더에 토큰이 없습니다.",
                                            "data": null
                                        }
                                        """)
                            }))
    })
    @Operation(summary = "채팅방에 해당하는 메세지 조회하기.", description = "채팅방에 해당하는 메세지들 조회합니다.")
    public ResponseEntity<?> getMessagesByRoomId(@PathVariable("roomId") String roomId);

    @Operation(summary = "채팅방 종료하기(웹소켓 close)", description = "채팅방을 종료합니다. 채팅 내역이 더 이상 조회되지 않습니다.")
    ResponseEntity<String> closeRoom(@PathVariable("roomId") String roomId);

    @Operation(summary = "채팅방 재시작하기(웹소켓 open)", description = "채팅방을 재시작합니다. 채팅 내역이 조회됩니다.")
    ResponseEntity<String> openRoom(@PathVariable("roomId") String roomId) ;

    @Operation(summary = "채팅방에 이미지 전송하기", description = "채팅방에 이미지를 전송합니다.")
    ResponseEntity<ChatMessageEntity> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("roomId") String roomId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 열린 채팅방을 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_FIND_CHATROOM\", \"message\": \"모든 채팅방을 조회했습니다.\", \"data\": [ { \"id\": 6, \"type\": \"TALK\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\", \"senderId\": 51, \"message\": \"반가워요\", \"timestamp\": \"2024-11-17T18:15:39.538526\", \"fileUrl\": null, \"fileName\": null, \"isApproved\": false }, { \"id\": 7, \"type\": \"TALK\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\", \"senderId\": 51, \"message\": \"ㅋㅎ\", \"timestamp\": \"2024-11-17T18:15:55.295389\", \"fileUrl\": null, \"fileName\": null, \"isApproved\": false }, { \"id\": 8, \"type\": \"RESERVATION\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\", \"senderId\": 51, \"message\": \"{\\\"id\\\":3,\\\"volunteerId\\\":51,\\\"caregiverId\\\":50,\\\"startTime\\\":\\\"2024-11-17T04:33:06.36\\\",\\\"endTime\\\":\\\"2024-11-17T04:33:06.36\\\",\\\"durationHours\\\":4,\\\"salary\\\":0,\\\"location\\\":\\\"경기도 용인시 수지구 어딘가\\\",\\\"mainTask\\\":\\\"설거지\\\",\\\"volunteerType\\\":\\\"VOLUNTEER_REQUEST\\\",\\\"roomId\\\":\\\"c0babffb-a39b-45e5-b326-a964b49e9409\\\"}\", \"timestamp\": \"2024-11-17T18:30:44.223335\", \"fileUrl\": null, \"fileName\": null, \"isApproved\": true }, { \"id\": 9, \"type\": \"RESERVATION\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\", \"senderId\": 50, \"message\": \"약속을 수락했어요!\", \"timestamp\": \"2024-11-17T18:19:04.822753\", \"fileUrl\": null, \"fileName\": null, \"isApproved\": true }, { \"id\": 23, \"type\": \"FILE\", \"roomId\": \"95c506c0-28f7-4877-80ad-6863ce2e1af9\", \"senderId\": 51, \"message\": \"사진을 보냈습니다.\", \"timestamp\": \"2024-11-17T22:03:20.713524\", \"fileUrl\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/cbf9f1dc-ff59-41e9-b612-e121eddab16c-KakaoTalk_20241114_101142529.jpg\", \"fileName\": \"KakaoTalk_20241114_101142529.jpg\", \"isApproved\": null } ] }"))),
            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = """
                                        {
                                            "status": 401,
                                            "code": "INVALID_ACCESS_TOKEN",
                                            "message": "유효하지 않은 토큰입니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_EXPIRED",
                                            "message": "토큰이 만료되었습니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_MISSING", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_MISSING",
                                            "message": "요청 헤더에 토큰이 없습니다.",
                                            "data": null
                                        }
                                        """)
                            })),
            @ApiResponse(responseCode = "404", description = "열린 채팅방이 존재하지 않을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"CHATROOMS_NOT_FOUND\", \"message\": \"열린 채팅방이 존재하지 않습니다.\", \"data\": null }")))
    })
    @Operation(summary = "열린 채팅방 조회하기", description = "열린 채팅방을 조회합니다.")
    public ResponseEntity<ResponseDTO<List<ChatRoomResponseDTO>>> findAllChatRooms(
            @AuthenticationPrincipal CustomUserDetails user);

}
