package com.carely.backend.controller.docs;

import com.carely.backend.dto.group.GetGroupDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface LikeAPI {
    @Operation(summary = "그룹 찜하기", description = "그룹을 찜합니다.")
    ResponseEntity<ResponseDTO> likeAddGroup(@PathVariable("groupId") Long groupId);

    @Operation(summary = "그룹 찜 취소하기", description = "그룹 찜을 취소합니다.")
    ResponseEntity<ResponseDTO> likeRemoveGroup(@PathVariable("groupId") Long groupId) ;

    @Operation(summary = "유저가 찜한 그룹 조회하기", description = "유저가 찜한 그룹 조회합니다.")
    ResponseEntity<ResponseDTO<List<GetGroupDTO.List>>> getAllLikedGroups();
    }
