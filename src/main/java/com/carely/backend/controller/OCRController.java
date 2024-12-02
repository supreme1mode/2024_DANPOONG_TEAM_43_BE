package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.dto.certificate.CertificateDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import com.carely.backend.service.ocr.OCRService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/certificates")
public class OCRController  {

    private final OCRService ocrService;

    public OCRController(OCRService ocrService) {
        this.ocrService = ocrService;
    }
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<?>> extractText(@RequestPart("file") MultipartFile file) throws Exception {
//        if (user.getUsername() == null) {
//            // 가입 안 된 유저... 1을 보내주거나 해야 함....
//            // 생년월일, 이름 있어야 함... 그래야 자격증 검증 가능할 듯?
//        }
//        else {
//            // 가입이 된 유저라면........... 검증을 하고 그 유저의 isCertificated 를 1로 바꿔줘야지
//            CertificateDTO certificateDTO = ocrService.extractTextAlreadyUser(file, user.getUsername());
//        }


        return ResponseEntity
                .status(SuccessCode.SUCCESS_OCR.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_OCR, null));
    }
}
