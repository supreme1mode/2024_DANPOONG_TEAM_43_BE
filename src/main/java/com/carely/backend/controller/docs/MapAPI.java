package com.carely.backend.controller.docs;

import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MapAPI {
    @Operation(summary = "지도에서 유저 목록 조회하기", description = "지도에서 유저 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO> findUsersByCityAndOptionalUserTypes(
            @RequestParam(value = "userType", required = false) List<UserType> userTypes);
}
