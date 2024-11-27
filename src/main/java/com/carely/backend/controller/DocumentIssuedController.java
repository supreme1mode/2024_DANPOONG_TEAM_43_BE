package com.carely.backend.controller;


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

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentIssuedController {
    public final DocumentIssuedService documentIssuedService;
//
//    @GetMapping("/{documentType}")
//    public ResponseEntity<ResponseDTO<?>> getDocumentIssuedList(@AuthenticationPrincipal CustomUserDetails user, @PathVariable String documentType) {
//        documentIssuedService.getDocumentIssuedList(user.getUsername(), documentType);
//
//    }
 }
