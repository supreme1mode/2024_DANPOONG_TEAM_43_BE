package com.carely.backend.controller.docs;

import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.response.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MapAPI {
    public ResponseEntity<ResponseDTO> findUsersByCityAndOptionalUserTypes(
            @RequestParam("city") String city,
            @RequestParam(value = "userType", required = false) List<UserType> userTypes);
}
