package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.OcrAPI;
import com.carely.backend.dto.ocr.OCRResponseDto;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.service.ocr.OCRService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ocr")
public class OCRController implements OcrAPI {

    private final OCRService ocrService;

    public OCRController(OCRService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<?>> extractText(@RequestPart("file") MultipartFile file) {
        OCRResponseDto result = ocrService.extractText(file);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_OCR.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_OCR, result));
    }
}
