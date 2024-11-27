package com.carely.backend.controller.docs;

import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface NewsAPI {
    @Operation(summary = "groupId에 해당하는 소식 목록 보기", description = "groupId에 해당하는 소식 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO> getGroupNewsList(@PathVariable("groupId") Long groupId);

    @Operation(summary = "소식 상세보기", description = "소식에 달린 답글과 함께 소식을 조회합니다.")
    public ResponseEntity<ResponseDTO> getNewsDetail(@PathVariable("newsId") Long newsId);
}
