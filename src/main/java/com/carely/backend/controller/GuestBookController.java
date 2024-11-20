package com.carely.backend.controller;


import com.carely.backend.code.SuccessCode;
import com.carely.backend.dto.guestBook.RequestGuestBookDTO;
import com.carely.backend.dto.guestBook.ResponseGuestBookDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import com.carely.backend.service.GuestBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("/guestbook"))
@RequiredArgsConstructor
public class GuestBookController {
    private final GuestBookService guestBookService;

    @PostMapping("/{id}")
    public ResponseEntity<ResponseDTO<?>> registerGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user, @RequestBody RequestGuestBookDTO requestGuestBookDTO, @PathVariable Long id) {
        guestBookService.registerGuestBook(requestGuestBookDTO, user.getUsername(), id);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_CREATE_GUESTBOOK.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_GUESTBOOK, null));
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<?>> getAllGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user){
        List<ResponseGuestBookDTO> responseGuestBookDTOList = guestBookService.getAllGuestBook(user.getUsername());

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK, responseGuestBookDTOList));
    }

    @GetMapping("/myHome")
    public ResponseEntity<ResponseDTO<?>> getGuestBookMyHome(@AuthenticationPrincipal CustomUserDetails user){
        List<ResponseGuestBookDTO> responseGuestBookDTOList = guestBookService.getGuestBookMyHome(user.getUsername());

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK, responseGuestBookDTOList));
    }

    @GetMapping("/caregiverHome")
    public ResponseEntity<ResponseDTO<?>> getGuestBookCaregiverHome(@AuthenticationPrincipal CustomUserDetails user) {
        List<ResponseGuestBookDTO> responseGuestBookDTOList = guestBookService.getGuestBookCaregiverHome(user.getUsername());

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK, responseGuestBookDTOList));
    }


  }
