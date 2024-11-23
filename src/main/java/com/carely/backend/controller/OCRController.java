package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.OcrAPI;
import com.carely.backend.dto.ocr.OCRCreateDTO;
import com.carely.backend.dto.ocr.OCRResponseDto;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import com.carely.backend.dto.user.RegisterDTO;
import com.carely.backend.service.ocr.OCRService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/certificates")
public class OCRController  {

    private final OCRService ocrService;

    public OCRController(OCRService ocrService) {
        this.ocrService = ocrService;
    }

//    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ResponseDTO<?>> extractText(
//            @RequestPart("file") MultipartFile file,
//            @RequestPart("ocrCreateDTO") @Valid OCRCreateDTO ocrCreateDTO) throws IOException {
//        OCRResponseDto result = ocrService.extractText(file, ocrCreateDTO.getUsername());
//        return ResponseEntity
//                .status(SuccessCode.SUCCESS_OCR.getStatus().value())
//                .body(new ResponseDTO<>(SuccessCode.SUCCESS_OCR, result));
//    }

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> registerUser(@RequestPart("file") MultipartFile file, @RequestPart("ocrCreateDTO") OCRCreateDTO ocrCreateDTO) throws IOException {
        OCRResponseDto result = ocrService.extractText(file, ocrCreateDTO.getUsername());
        return ResponseEntity
                .status(SuccessCode.SUCCESS_OCR.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_OCR, result));
    }
}
