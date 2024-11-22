package com.carely.backend.controller.docs;

import com.carely.backend.dto.memo.CreateMemoDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemoAPI {
    @Operation(summary = "방명록 작성하기(메모)", description = "방명록을 작성합니다. 작성한 방명록은 ai가 요약합니다.")
    public ResponseEntity<ResponseDTO> createMemo(@RequestBody CreateMemoDTO createMemoDTO);

//    @Operation(summary = "방명록 조회하기", description = "다른 유저가 작성한 방명록을 조회합니다.")
//    public ResponseEntity<ResponseDTO> getMemoList(@PathVariable("userId") Long userId);

    @Operation(summary = "메모를 작성하지 않은 약속 조회하기", description = "메모를 작성하지 않은 약속 조회하기")
    public ResponseEntity<ResponseDTO> getNotWrittenVolunteer();

    @Operation(summary = "유저의 요약된 메모 조회하기", description = "유저의 요약된 메모 조회하기")
    public ResponseEntity<ResponseDTO> getRecentMemo(@PathVariable("userId") Long userId);
}
