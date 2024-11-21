package com.carely.backend.controller;


import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.LikeAPI;
import com.carely.backend.dto.group.GetGroupDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController implements LikeAPI {
    private final LikeService likeService;

    @PostMapping("/add/{groupId}")
    public ResponseEntity<ResponseDTO> likeAddGroup(@PathVariable("groupId") Long groupId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        likeService.likeAddGroup(kakaoId, groupId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_ADD_LIKE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_ADD_LIKE, null));
    }

    @PostMapping("/remove/{groupId}")
    public ResponseEntity<ResponseDTO> likeRemoveGroup(@PathVariable("groupId") Long groupId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        likeService.likeRemoveGroup(kakaoId, groupId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_REMOVE_LIKE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REMOVE_LIKE, null));
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<List<GetGroupDTO.List>>> getAllLikedGroups() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<GetGroupDTO.List> res = likeService.getAllLikedGroups(kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER_GROUP.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER_GROUP, res));
    }
}
