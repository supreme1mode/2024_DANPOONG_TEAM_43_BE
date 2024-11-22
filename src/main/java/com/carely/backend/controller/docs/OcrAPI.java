package com.carely.backend.controller.docs;

import com.carely.backend.dto.ocr.OCRResponseDto;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
public interface OcrAPI {

    @Operation(summary = "OCR 검증하기", description = "파일을 올리면 검증된 결과가 나옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "서류 OCR을 성공했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OCRResponseDto.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 200,\n" +
                                    "  \"code\": \"SUCCESS_OCR\",\n" +
                                    "  \"message\": \"성공적으로 서류 검출을 진행했습니다.\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"name\": \"최은영\",\n" +
                                    "    \"birth\": \"1987년 05월 23일\",\n" +
                                    "    \"certificateNum\": \"CA228182213424\",\n" +
                                    "    \"certificateType\": \"요양보호자격\",\n" +
                                    "    \"certificateDate\": \"2024년 11월 20일\",\n" +
                                    "    \"certificateName\": \"CA케어매니저자격 1급\"\n" +
                                    "  }\n" +
                                    "}")
                    )
            ),
            @ApiResponse(responseCode = "409", description = "데이터베이스에 중복된 유저 이름이 있는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 409,\n" +
                                    "  \"error\": \"CONFLICT\",\n" +
                                    "  \"code\": \"DUPLICATE_USERNAME\",\n" +
                                    "  \"message\": \"중복된 유저 이름입니다.\"\n" +
                                    "}")
                    )
            )
    })
    ResponseEntity<ResponseDTO<?>> extractText(@Valid @RequestPart("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails user);
}
