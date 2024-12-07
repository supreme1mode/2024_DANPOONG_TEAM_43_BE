package com.carely.backend.controller.docs;


import com.carely.backend.dto.group.CreateGroupDTO;
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
import org.springframework.web.bind.annotation.RequestPart;

import java.io.IOException;

public interface GroupAPI {

    @Operation(summary = "   그룹 생성하기", description = "새로운 그룹을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "그룹을 성공적으로 생성했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": 201,
                                    "code": "SUCCESS_CREATE_GROUP",
                                    "message": "그룹을 성공적으로 생성했습니다.",
                                    "data": {
                                        "groupId": 2,
                                        "groupName": "그룹이름",
                                        "city": "도봉구",
                                        "description": "상세 설명",
                                        "schedule": "스케쥴",
                                        "headCount": 0,
                                        "userType": "VOLUNTEER",
                                        "groupImage": "https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/d9ef4300-6bfb-4315-9023-7093c4236901"
                                    }
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
    ResponseEntity<ResponseDTO> createGroup(@RequestPart("createGroupDTO") CreateGroupDTO createGroupDTO) throws IOException;

    @Operation(summary = "그룹 삭제하기", description = "그룹을 만든 사람이 그룹을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹에 성공적으로 가입했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": 200,
                                    "code": "SUCCESS_JOIN_GROUP",
                                    "message": "그룹에 성공적으로 가입되었습니다.",
                                    "data": {
                                        "groupId": 1,
                                        "groupName": "그룹이름",
                                        "city": "도봉구",
                                        "description": "상세 설명",
                                        "schedule": "스케쥴",
                                        "headCount": 1,
                                        "isLiked": false,
                                        "isWriter": true,
                                        "groupImage": "https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/f905c359-2fc3-4a87-96ea-9a5d343cd006",
                                        "users": [
                                            {
                                                "userId": 1,
                                                "username": "김은서",
                                                "userType": "CAREGIVER"
                                            }
                                        ]
                                    }
                                }
                                """))),

            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없는 경우 또는 사용자를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "GROUP_NOT_FOUND", value = """
                                        {
                                            "status": 404,
                                            "error": "NOT_FOUND",
                                            "code": "GROUP_NOT_FOUND",
                                            "message": "그룹을 찾을 수 없습니다."
                                        }
                                        """),
                                    @ExampleObject(name = "USER_NOT_FOUND", value = """
                                        {
                                            "status": 404,
                                            "code": "USER_NOT_FOUND",
                                            "message": "사용자를 찾을 수 없습니다."
                                        }
                                        """)
                            })),

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
    ResponseEntity<ResponseDTO> deleteGroup(@PathVariable("groupId") Long groupId) ;

    @Operation(summary = "그룹 가입하기", description = "그룹에 가입합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹에 성공적으로 가입했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": 200,
                                    "code": "SUCCESS_JOIN_GROUP",
                                    "message": "그룹에 성공적으로 가입되었습니다.",
                                    "data": {
                                        "groupId": 1,
                                        "groupName": "그룹이름",
                                        "city": "도봉구",
                                        "description": "상세 설명",
                                        "schedule": "스케쥴",
                                        "headCount": 1,
                                        "isLiked": false,
                                        "isWriter": true,
                                        "groupImage": "https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/f905c359-2fc3-4a87-96ea-9a5d343cd006",
                                        "users": [
                                            {
                                                "userId": 1,
                                                "username": "김은서",
                                                "userType": "CAREGIVER"
                                            }
                                        ]
                                    }
                                }
                                """))),

            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없는 경우 또는 사용자를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "GROUP_NOT_FOUND", value = """
                                        {
                                            "status": 404,
                                            "error": "NOT_FOUND",
                                            "code": "GROUP_NOT_FOUND",
                                            "message": "그룹을 찾을 수 없습니다."
                                        }
                                        """),
                                    @ExampleObject(name = "USER_NOT_FOUND", value = """
                                        {
                                            "status": 404,
                                            "code": "USER_NOT_FOUND",
                                            "message": "사용자를 찾을 수 없습니다."
                                        }
                                        """)
                            })),

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
    ResponseEntity<ResponseDTO> joinGroup(@PathVariable("groupId") Long groupId) ;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 그룹 탈퇴한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_CANCEL_JOIN_GROUP\", \"message\": \"그룹 가입이 성공적으로 취소되었습니다.\", \"data\": { \"groupId\": 1, \"groupName\": \"그룹이름\", \"city\": \"도봉구\", \"description\": \"상세 설명\", \"schedule\": \"스케쥴\", \"headCount\": 0 } }"))),

            @ApiResponse(responseCode = "404", description = "해당 groupId를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"GROUP_NOT_FOUND\", \"message\": \"해당 그룹을 찾을 수 없습니다.\", \"data\": null }"))),

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
    @Operation(summary = "그룹 탈퇴하기", description = "그룹을 나갑니다.")
    ResponseEntity<ResponseDTO> leaveGroup(@PathVariable("groupId") Long groupId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 유저가 가입한 그룹을 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_REGISTER_GROUP\", \"message\": \"그룹을 성공적으로 조회했습니다.\", \"data\": { \"groupId\": 4, \"groupName\": \"그룹이름\", \"city\": \"도봉구\", \"description\": \"상세 설명\", \"schedule\": \"스케쥴\", \"headCount\": 1, \"isLiked\": true, \"isWriter\": true, \"groupImage\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/057d5abb-6b91-4754-bdea-e5778009e3e4\" } }"))),

            @ApiResponse(responseCode = "404", description = "유저가 가입한 그룹이 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"GROUP_NOT_FOUND\", \"message\": \"유저가 가입한 그룹을 찾을 수 없습니다.\", \"data\": null }"))),

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
    @Operation(summary = "유저가 가입한 그룹 조회하기", description = "유저가 가입한 그룹을 조회합니다.")
    ResponseEntity<ResponseDTO> getUserJoinedGroups();

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 그룹 목록을 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_REGISTER_GROUP\", \"message\": \"그룹을 성공적으로 조회했습니다.\", \"data\": [ { \"groupId\": 3, \"groupName\": \"그룹이름\", \"city\": \"도봉구\", \"description\": \"상세 설명\", \"headCount\": 0, \"userType\": \"VOLUNTEER\", \"isLiked\": false, \"isWriter\": true, \"groupImage\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/a08c7664-a0a9-49f2-9180-a04f2fa69582\" }, { \"groupId\": 4, \"groupName\": \"그룹이름\", \"city\": \"도봉구\", \"description\": \"상세 설명\", \"headCount\": 0, \"userType\": \"VOLUNTEER\", \"isLiked\": true, \"isWriter\": true, \"groupImage\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/057d5abb-6b91-4754-bdea-e5778009e3e4\" }, { \"groupId\": 5, \"groupName\": \"그룹이름\", \"city\": \"도봉구\", \"description\": \"상세 설명\", \"headCount\": 0, \"userType\": \"VOLUNTEER\", \"isLiked\": true, \"isWriter\": true, \"groupImage\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/eaef69d2-6b7d-4aa2-a1f3-45cc5246b28b\" }, { \"groupId\": 6, \"groupName\": \"그룹이름\", \"city\": \"도봉구\", \"description\": \"상세 설명\", \"headCount\": 0, \"userType\": \"VOLUNTEER\", \"isLiked\": false, \"isWriter\": true, \"groupImage\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/f314d720-ec42-4bc9-8360-e2197282a636\" }, { \"groupId\": 7, \"groupName\": \"그룹이름\", \"city\": \"도봉구\", \"description\": \"상세 설명\", \"headCount\": 0, \"userType\": \"CAREGIVER\", \"isLiked\": false, \"isWriter\": true, \"groupImage\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/5d1934e4-38b3-4a2b-a56c-44ccd684dc89\" } ] }"))),

            @ApiResponse(responseCode = "404", description = "조회 가능한 그룹이 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"GROUP_NOT_FOUND\", \"message\": \"조회 가능한 그룹이 없습니다.\", \"data\": null }"))),

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
    @Operation(summary = "그룹 목록 조회하기", description = "유저가 가입하지 않은 다른 그룹을 조회합니다.")
    ResponseEntity<ResponseDTO> getGroupList();

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 그룹 상세 정보를 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_REGISTER_GROUP\", \"message\": \"그룹을 성공적으로 조회했습니다.\", \"data\": { \"groupId\": 4, \"groupName\": \"그룹이름\", \"city\": \"도봉구\", \"description\": \"상세 설명\", \"schedule\": \"스케쥴\", \"headCount\": 1, \"isLiked\": true, \"isWriter\": true, \"groupImage\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/group/057d5abb-6b91-4754-bdea-e5778009e3e4\" } }"))),

            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"GROUP_NOT_FOUND\", \"message\": \"그룹을 찾을 수 없습니다.\", \"data\": null }"))),

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
    @Operation(summary = "그룹에 대한 상세 정보 조회하기", description = "그룹에 대한 상세 정보를 조회합니다.")
    ResponseEntity<ResponseDTO> getGroupDetail(@PathVariable("groupId") Long groupId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 그룹에 가입한 유저를 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_USER\", \"message\": \"유저 정보 조회를 성공했습니다.\", \"data\": [ { \"userId\": 91, \"username\": \"이규민\", \"userType\": \"CAREGIVER\", \"timeTogether\": 0, \"age\": 26 }, { \"userId\": 3, \"username\": \"이정민\", \"userType\": \"CAREGIVER\", \"timeTogether\": 0, \"age\": 52 }, { \"userId\": 6, \"username\": \"김세훈\", \"userType\": \"VOLUNTEER\", \"timeTogether\": 0, \"age\": 33 }, { \"userId\": 13, \"username\": \"정현미\", \"userType\": \"CARE_WORKER\", \"timeTogether\": 0, \"age\": 33 }, { \"userId\": 4, \"username\": \"최미선\", \"userType\": \"CAREGIVER\", \"timeTogether\": 0, \"age\": 42 } ] }"))),

            @ApiResponse(responseCode = "404", description = "그룹에 가입한 유저가 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"그룹에 가입한 유저를 찾을 수 없습니다.\", \"data\": null }"))),

            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청으로 조회 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 401, \"code\": \"UNAUTHORIZED\", \"message\": \"유저 인증에 실패했습니다.\", \"data\": null }")))
    })
    @Operation(summary = "그룹에 가입한 사람 조회하기", description = "그룹에 가입한 사람을 조회합니다.")
    public ResponseEntity<ResponseDTO> getGroupUser(@PathVariable("groupId") Long groupId);

}

