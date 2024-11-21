package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.MemoAPI;
import com.carely.backend.dto.memo.CreateMemoDTO;
import com.carely.backend.dto.memo.MemoResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.volunteer.GetVolunteerInfoDTO;
import com.carely.backend.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/memo")
public class MemoController implements MemoAPI {
    private final MemoService memoService;

    @PostMapping("")
    public ResponseEntity<ResponseDTO> createMemo(@RequestBody CreateMemoDTO createMemoDTO) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        MemoResponseDTO.List res = memoService.createMemo(kakaoId, createMemoDTO.getContent(), createMemoDTO.getVolunteerId());

        return ResponseEntity
                .status(SuccessCode.SUCCESS_CREATE_MEMO.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_MEMO, res));
    }

    @GetMapping("/not-written")
    public ResponseEntity<ResponseDTO> getNotWrittenVolunteer() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<GetVolunteerInfoDTO> res = memoService.getVolunteerInfo(kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_VOLUNTEER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_VOLUNTEER, res));
    }

//    @GetMapping("/{userId}")
//    public ResponseEntity<ResponseDTO> getMemoList(@PathVariable("userId") Long userId) {
//        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        List<MemoResponseDTO.List> res = memoService.getMemoList(kakaoId, userId);
//
//        return ResponseEntity
//                .status(SuccessCode.SUCCESS_RETRIEVE_MEMO.getStatus().value())
//                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_MEMO, res));
//    }


    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO> getRecentMemo(@PathVariable("userId") Long userId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<MemoResponseDTO.List> res = memoService.getMemoList(kakaoId, userId);

        if(res.isEmpty()) {
            return ResponseEntity
                    .status(SuccessCode.SUCCESS_RETRIEVE_MEMO.getStatus().value())
                    .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_MEMO, res));
        }

        MemoResponseDTO.List oneRes = res.get(res.size() - 1);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_MEMO.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_MEMO, oneRes));
    }
}
