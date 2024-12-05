package com.carely.backend.controller;


import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.GuestBookAPI;
import com.carely.backend.dto.guestBook.RequestGuestBookDTO;
import com.carely.backend.dto.guestBook.ResponseGroupGuestbookDTO;
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
public class GuestBookController implements GuestBookAPI {
    private final GuestBookService guestBookService;

    @PostMapping("/{id}")
    public ResponseEntity<ResponseDTO<?>> registerGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user, @RequestBody RequestGuestBookDTO requestGuestBookDTO, @PathVariable Long id) {
        guestBookService.registerGuestBook(requestGuestBookDTO, user.getUsername(), id);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_CREATE_GUESTBOOK.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_GUESTBOOK, null));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<?>> deleteGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id) {
        guestBookService.deleteGuestBook(user.getUsername(), id);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_DELETE_GUESTBOOK.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_GUESTBOOK, null));
    }

    // 그룹에 속한 방명록 조회
    @GetMapping("/guestbook/{groupId}")
    public ResponseEntity<ResponseDTO<?>> getGroupGuestbook(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails user) {
        List<ResponseGroupGuestbookDTO> res = guestBookService.getGroupGuestbook(groupId, user.getUsername());

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK, res));
    }
    
    
    // 마이페이지 방명록 조회
    @GetMapping("/myPage")
    public ResponseEntity<ResponseDTO<?>> getMyPageGuestBook(@AuthenticationPrincipal CustomUserDetails user) {
        List<ResponseGroupGuestbookDTO> res = guestBookService.getMyPageGuestBook(user.getUsername());

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK, res));
    }

    //    @GetMapping("/all")
//    public ResponseEntity<ResponseDTO<?>> getAllGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user){
//        List<ResponseGuestBookDTO> responseGuestBookDTOList = guestBookService.getAllGuestBook(user.getUsername());
//
//        return ResponseEntity
//                .status(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK.getStatus().value())
//                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK, responseGuestBookDTOList));
//    }
//
//    @GetMapping("/myHome")
//    public ResponseEntity<ResponseDTO<?>> getGuestBookMyHome(@Valid @AuthenticationPrincipal CustomUserDetails user){
//        List<ResponseGuestBookDTO> responseGuestBookDTOList = guestBookService.getGuestBookMyHome(user.getUsername());
//
//        return ResponseEntity
//                .status(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK.getStatus().value())
//                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK, responseGuestBookDTOList));
//    }
//
//    @GetMapping("/caregiverHome")
//    public ResponseEntity<ResponseDTO<?>> getGuestBookCaregiverHome(@Valid @AuthenticationPrincipal CustomUserDetails user) {
//        List<ResponseGuestBookDTO> responseGuestBookDTOList = guestBookService.getGuestBookCaregiverHome(user.getUsername());
//
//        return ResponseEntity
//                .status(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK.getStatus().value())
//                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_GUESTBOOK, responseGuestBookDTOList));
//    }

}
