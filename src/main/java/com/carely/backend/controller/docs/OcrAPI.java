package com.carely.backend.controller.docs;

import com.carely.backend.dto.ocr.OCRResponseDto;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            @ApiResponse(responseCode = "401", description = "회원 정보와 자격증 정보가 일치하지 않을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"status\": 401,\n" +
                                    "  \"code\": \"USER_NOT_MATCH\",\n" +
                                    "  \"message\": \"자격증의 정보가 가입한 회원정보와 일치하지 않습니다.\"\n" +
                                    "}")
                    )
            )
    })
    ResponseEntity<ResponseDTO<?>> extractText(@Valid @RequestPart("file") MultipartFile file);
}
