package com.carely.backend.controller.docs;

import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MapAPI {
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 유저 목록을 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_USER\", \"message\": \"유저 정보 조회를 성공했습니다.\", \"data\": [ { \"userId\": 1, \"username\": \"박수정\", \"city\": \"용인시 수지구\", \"address\": \"경기도 용인시 수지구 고기동 15\", \"detailAddress\": \"202호\", \"userType\": \"CAREGIVER\", \"shareLocation\": true, \"latitude\": 37.3418, \"longitude\": 127.0951, \"talk\": \"보통\", \"eat\": \"보통\", \"toilet\": \"서투름\", \"bath\": \"수월\", \"walk\": \"수월\", \"togetherTime\": 0, \"km\": 6.354688064803219 }, { \"userId\": 2, \"username\": \"김영진\", \"city\": \"용인시 수지구\", \"address\": \"경기도 용인시 수지구 고기동 산16-2\", \"detailAddress\": \"202호\", \"userType\": \"CAREGIVER\", \"shareLocation\": true, \"latitude\": 37.342, \"longitude\": 127.0949, \"talk\": \"수월\", \"eat\": \"수월\", \"toilet\": \"보통\", \"bath\": \"서투름\", \"walk\": \"보통\", \"togetherTime\": 0, \"km\": 6.339194156654298 } ] }"))),

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
            @ApiResponse(responseCode = "404", description = "해당 조건에 맞는 유저를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"조건에 맞는 유저를 찾을 수 없습니다.\", \"data\": null }")))
    })
    @Operation(summary = "지도에서 유저 목록 조회하기", description = "지도에서 유저 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO> findUsersByCityAndOptionalUserTypes(
            @RequestParam(value = "userType", required = false) List<UserType> userTypes);

}
