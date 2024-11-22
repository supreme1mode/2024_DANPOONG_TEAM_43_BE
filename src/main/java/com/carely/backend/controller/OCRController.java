package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.OcrAPI;
import com.carely.backend.dto.ocr.OCRResponseDto;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import com.carely.backend.service.ocr.OCRService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/certificates")
public class OCRController implements OcrAPI {

    private final OCRService ocrService;

    public OCRController(OCRService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<?>> extractText(@Valid @RequestPart("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails user) {
        OCRResponseDto result = ocrService.extractText(file, user.getUsername());
        return ResponseEntity
                .status(SuccessCode.SUCCESS_OCR.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_OCR, result));
    }
}
