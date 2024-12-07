package com.carely.backend.controller;


import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.DocumentAPI;
import com.carely.backend.dto.document.ResponseDocumentDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import com.carely.backend.service.DocumentIssuedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentIssuedController implements DocumentAPI {
    public final DocumentIssuedService documentIssuedService;

    @GetMapping("/{documentType}/userId")
    public ResponseEntity<ResponseDTO<?>> getDocumentIssuedList(@PathVariable String documentType, @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println(user.getUsername());
        List<ResponseDocumentDTO> list = documentIssuedService.getDocumentIssuedList(user.getUsername(), documentType);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_ACTIVITY_LIST.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_ACTIVITY_LIST, list));
    }
 }
