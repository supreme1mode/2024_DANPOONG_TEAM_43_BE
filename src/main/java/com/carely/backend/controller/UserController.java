package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.RegisterDTO;
import com.carely.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> registerUser(@RequestPart("registerDTO") RegisterDTO registerDTO, @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        RegisterDTO.Res res = userService.register(registerDTO, image);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER, res));
    }
}
