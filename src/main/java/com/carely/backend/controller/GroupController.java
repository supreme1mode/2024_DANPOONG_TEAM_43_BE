package com.carely.backend.controller;


import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.GroupAPI;
import com.carely.backend.dto.group.CreateGroupDTO;
import com.carely.backend.dto.group.GetGroupDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.UserResponseDTO;
import com.carely.backend.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController implements GroupAPI {
    private final GroupService groupService;

    // 그룹 생성하기
    @PostMapping(value = "/create")
    public ResponseEntity<ResponseDTO> createGroup(@RequestBody CreateGroupDTO createGroupDTO) throws IOException {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        CreateGroupDTO.Res res = groupService.createCaregiverGroup(kakaoId, createGroupDTO);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_CREATE_GROUP.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_GROUP, res));
    }

    // 그룹 목록 조회하기
    @GetMapping("/list")
    public ResponseEntity<ResponseDTO> getGroupList() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<GetGroupDTO.GroupList> res = groupService.getGroupList(kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER_GROUP.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER_GROUP, res));
    }

    @GetMapping("/detail/{groupId}")
    public ResponseEntity<ResponseDTO> getGroupDetail(@PathVariable("groupId") Long groupId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        GetGroupDTO.Detail res = groupService.getGroupDetail(groupId, kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER_GROUP.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER_GROUP, res));
    }

    @PostMapping("/join/{groupId}")
    public ResponseEntity<ResponseDTO> joinGroup(@PathVariable("groupId") Long groupId) {

        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        GetGroupDTO.List res = groupService.joinGroup(kakaoId, groupId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_JOIN_GROUP.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_JOIN_GROUP, res));
    }

    //
    @PostMapping("/leave/{groupId}")
    public ResponseEntity<ResponseDTO> leaveGroup(@PathVariable("groupId") Long groupId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        GetGroupDTO.List res = groupService.leaveGroup(kakaoId, groupId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_LEAVE_GROUP.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_LEAVE_GROUP, res));
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<ResponseDTO> deleteGroup(@PathVariable("groupId") Long groupId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupService.deleteGroup(kakaoId, groupId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_DELETE_GROUP.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_GROUP, null));
    }

    @GetMapping()
    public ResponseEntity<ResponseDTO> getUserJoinedGroups() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<GetGroupDTO.GroupList> res = groupService.getUserJoinedGroups(kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER_GROUP.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER_GROUP, res));
    }

    // 그룹에 속한 유저 조회
    @GetMapping("/user/{groupId}")
    public ResponseEntity<ResponseDTO> getGroupUser(@PathVariable("groupId") Long groupId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();


        List<UserResponseDTO.Group> res = groupService.getGroupUser(groupId, kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, res));
    }

}
