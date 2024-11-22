package com.carely.backend.controller.docs;

import com.carely.backend.dto.chat.ChatRequest;
import com.carely.backend.dto.guestBook.RequestGuestBookDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface GuestBookAPI {
    @Operation(summary = "방명록 생성하기", description = "방명록을 생성합니다. volunteer_id를 넣어주세요. 한 id에 하나의 방명록만 작성할 수 있습니다.")
    public ResponseEntity<?> registerGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user, @RequestBody RequestGuestBookDTO requestGuestBookDTO, @PathVariable Long id);

    @Operation(summary = "방명록 조회하기", description = "전체 방명록을 조회합니다.")
    public ResponseEntity<?> getAllGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user);

    @Operation(summary = "방명록 내 집만 조회하기", description = "내가 caregiver인 방명록을 조회합니다.")
    public ResponseEntity<ResponseDTO<?>> getGuestBookMyHome(@Valid @AuthenticationPrincipal CustomUserDetails user);


    @Operation(summary = "방명록 이웃의 집만 조회하기", description = "내가 volunteer인 방명록을 조회합니다.")
    public ResponseEntity<ResponseDTO<?>> getGuestBookCaregiverHome(@Valid @AuthenticationPrincipal CustomUserDetails user);

    @Operation(summary = "방명록 삭제하기", description = "방명록을 삭제합니다. volunteer_id를 넣어주세요. 본인이 volunteer로 작성한 방명록만 삭제가 가능합니다.")
    
    public ResponseEntity<ResponseDTO<?>> deleteGuestBook(@Valid @AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id);


}
