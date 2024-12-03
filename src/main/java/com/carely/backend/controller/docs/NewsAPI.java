package com.carely.backend.controller.docs;

import com.carely.backend.dto.news.CreateCommentDTO;
import com.carely.backend.dto.news.CreateNewsDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface NewsAPI {
    @Operation(summary = "groupId에 해당하는 소식 목록 보기", description = "groupId에 해당하는 소식 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO> getGroupNewsList(@PathVariable("groupId") Long groupId);

    @Operation(summary = "소식 상세보기", description = "소식에 달린 답글과 함께 소식을 조회합니다.")
    public ResponseEntity<ResponseDTO> getNewsDetail(@PathVariable("newsId") Long newsId);

    @Operation(summary = "소식 생성하기", description = "소식을 생성합니다.")
    public ResponseEntity<ResponseDTO<CreateNewsDTO.Res>> createNews(@PathVariable("groupId") Long groupId, @Valid @RequestBody CreateNewsDTO createNewsDTO);

    @Operation(summary = "소식에 댓글 남기기", description = "소식에 댓글을 남깁니다.")
    public ResponseEntity<ResponseDTO<CreateCommentDTO.Res>> createNewsComment(@PathVariable("newsId") Long newsId, @Valid @RequestBody CreateCommentDTO createNewsDTO);
    }
