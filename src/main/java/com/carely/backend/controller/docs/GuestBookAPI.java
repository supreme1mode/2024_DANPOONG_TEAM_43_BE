package com.carely.backend.controller.docs;

import com.carely.backend.dto.chat.ChatRequest;
import com.carely.backend.dto.guestBook.RequestGuestBookDTO;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface GuestBookAPI {
    @Operation(summary = "방명록 생성하기", description = "방명록을 생성합니다. volunteer_id를 넣어주세요. 한 id에 하나의 방명록만 작성할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "그룹을 성공적으로 생성했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 200,
                                      "code": "SUCCESS_CREATE_GUESTBOOK",
                                      "message": "방명록을 성공적으로 생성했습니다.",
                                      "data": null
                                    }
                                    """))),

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

            @ApiResponse(responseCode = "404", description = "존재하지 않는 access Token으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = """
                                            {
                                                "status": 404,
                                                "code": "USER_NOT_FOUND",
                                                "message": "사용자를 찾을 수 없습니다.",
                                                "data": null
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "406", description = "이미 방명록이 존재하는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = """
                                    {
                                      "status": 406,
                                      "error": "NOT_ACCEPTABLE",
                                      "code": "ALREADY_EXISTS_GUESTBOOK",
                                      "message": "이미 해당 활동에 대한 방명록이 존재합니다."
                                    }
                                            """)
                            }))
    })
    public ResponseEntity<?> registerGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user, @RequestBody RequestGuestBookDTO requestGuestBookDTO, @PathVariable Long id);

//    @Operation(summary = "방명록 조회하기", description = "전체 방명록을 조회합니다.")
//    public ResponseEntity<?> getAllGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user);
//
//    @Operation(summary = "방명록 내 집만 조회하기", description = "내가 caregiver인 방명록을 조회합니다.")
//    public ResponseEntity<ResponseDTO<?>> getGuestBookMyHome(@Valid @AuthenticationPrincipal CustomUserDetails user);
//
//
//    @Operation(summary = "방명록 이웃의 집만 조회하기", description = "내가 volunteer인 방명록을 조회합니다.")
//    public ResponseEntity<ResponseDTO<?>> getGuestBookCaregiverHome(@Valid @AuthenticationPrincipal CustomUserDetails user);

    @Operation(summary = "방명록 삭제하기", description = "방명록을 삭제합니다. volunteer_id를 넣어주세요. 본인이 volunteer로 작성한 방명록만 삭제가 가능합니다.")
    
    public ResponseEntity<ResponseDTO<?>> deleteGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id);

    @Operation(summary = "그룹 방명록 불러오기", description = "group_id에 있는 방명록을 모두 불러옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방명록을 성공적으로 조회했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                  "status": 200,
                                  "code": "SUCCESS_RETRIEVE_GUESTBOOK",
                                  "message": "방명록을 성공적으로 조회했습니다.",
                                  "data": [
                                    {
                                      "volunteer": {
                                        "userType": "CARE_WORKER",
                                        "username": "강신영",
                                        "content": "어우 지겨워"
                                      },
                                      "caregiver": {
                                        "userType": "CAREGIVER",
                                        "username": "김민주",
                                        "content": null
                                      }
                                    }
                                  ]
                                }
                                """))),
            @ApiResponse(responseCode = "200", description = "방명록을 성공적으로 조회했으나 리스트가 비어있을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "status": 200,
                              "code": "SUCCESS_BUT_LIST_EMPTY",
                              "message": "성공적으로 조회하였으나, 리스트가 비었습니다.",
                              "data": null
                            }
                                """))),

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

            @ApiResponse(responseCode = "404", description = "존재하지 않는 access Token으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "code": "USER_NOT_FOUND",
                                            "message": "사용자를 찾을 수 없습니다.",
                                            "data": null
                                        }
                                        """)
                            }))
    })
    public ResponseEntity<ResponseDTO<?>> getGroupGuestbook(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails user);



    @Operation(summary = "방명록 조회하기", description = "마이페이지에 있는 방명록 조회하기.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방명록을 성공적으로 조회했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                  "status": 200,
                                  "code": "SUCCESS_RETRIEVE_GUESTBOOK",
                                  "message": "방명록을 성공적으로 조회했습니다.",
                                  "data": [
                                    {
                                      "volunteer": {
                                        "userType": "CARE_WORKER",
                                        "username": "강신영",
                                        "content": "어우 지겨워"
                                      },
                                      "caregiver": {
                                        "userType": "CAREGIVER",
                                        "username": "김민주",
                                        "content": null
                                      }
                                    }
                                  ]
                                }
                                """))),
            @ApiResponse(responseCode = "200", description = "방명록을 성공적으로 조회했으나 리스트가 비어있을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "status": 200,
                              "code": "SUCCESS_BUT_LIST_EMPTY",
                              "message": "성공적으로 조회하였으나, 리스트가 비었습니다.",
                              "data": null
                            }
                                """))),

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

            @ApiResponse(responseCode = "404", description = "존재하지 않는 access Token으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "code": "USER_NOT_FOUND",
                                            "message": "사용자를 찾을 수 없습니다.",
                                            "data": null
                                        }
                                        """)
                            }))
    })
    public ResponseEntity<ResponseDTO<?>> getMyPageGuestBook(@AuthenticationPrincipal CustomUserDetails user);


}
