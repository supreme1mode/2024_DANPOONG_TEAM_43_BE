package com.carely.backend.controller.docs;

import com.carely.backend.dto.easyCodef.AdditionalAuthDTO;
import com.carely.backend.dto.easyCodef.RequestUserIdentityDTO;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.service.EasyCodef.EasyCodefResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;

public interface EasyCodefAPI {
    @Operation(summary = "주민번호 인증", description = "주민등록번호를 인증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주민번호 인증에 성공했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": 200,
                                    "code": "SUCCESS_GET_IDENTITY",
                                    "message": "주민등록번호가 인증되었습니다.",
                                    "data": null
                                }
                                """))),

            @ApiResponse(responseCode = "404", description = "주민등록번호가 유효하지 않거나, 인증되지 않은 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "code": "NOT_IDENTITY_USER",
                                            "message": "주민등록번호가 유효하지 않습니다..",
                                            "data": null
                                        }
                                        """)
                            }))
    })
    public ResponseEntity<ResponseDTO> connectAPI(@RequestBody RequestUserIdentityDTO requestUserIdentityDTO) throws InterruptedException, UnsupportedEncodingException, JsonProcessingException;
}
