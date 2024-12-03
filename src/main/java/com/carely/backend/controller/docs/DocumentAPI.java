package com.carely.backend.controller.docs;

import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface DocumentAPI {

    @Operation(summary = "봉사 및 요양보호 활동 불러오기(주민번호가 있어야 합니다!!!!!!!!)", description = "봉사 및 요양보호 활동 리스트를 불러옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리스트를 성공적으로 불러왔을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": 200,
                                    "code": "SUCCESS_RETRIEVE_ACTIVITY_LIST",
                                    "message": "활동 목록을 성공적으로 조회하였습니다.",
                                    "data": [
                                        {
                                            "myType": "CARE_WORKER",
                                            "myName": "강신영",
                                            "volunteerSessionType": "VOLUNTEER",
                                            "partnerType": "CAREGIVER",
                                            "partnerName": "김금서",
                                            "volunteerDate": "2024-12-03",
                                            "myIdentity": "123454567890",
                                            "address": "서울 광진구 천호대로136길 33",
                                            "durationTimes": 80,
                                            "content": "string"
                                        },
                                        {
                                            "myType": "CARE_WORKER",
                                            "myName": "강신영",
                                            "volunteerSessionType": "VOLUNTEER",
                                            "partnerType": "CAREGIVER",
                                            "partnerName": "김금서",
                                            "volunteerDate": "2024-12-03",
                                            "myIdentity": "123454567890",
                                            "address": "서울 광진구 천호대로136길 33",
                                            "durationTimes": 80,
                                            "content": "string"
                                        }
                                    ]
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
    public ResponseEntity<ResponseDTO<?>> getDocumentIssuedList(@PathVariable String documentType, @PathVariable Long userId);

}
