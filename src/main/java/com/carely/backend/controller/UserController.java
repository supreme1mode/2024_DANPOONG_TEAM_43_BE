package com.carely.backend.controller;

import com.carely.backend.code.SuccessCode;
import com.carely.backend.domain.RefreshEntity;
import com.carely.backend.domain.User;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.CustomUserDetails;
import com.carely.backend.dto.user.MyPageDTO;
import com.carely.backend.dto.user.NotUserDTO;
import com.carely.backend.dto.user.RegisterDTO;
import com.carely.backend.jwt.JWTUtil;
import com.carely.backend.repository.RefreshRepository;
import com.carely.backend.service.UserService;
import com.carely.backend.service.kakao.KakaoLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final KakaoLoginService kakaoLoginService;
    private final RefreshRepository refreshRedisRepository;
    private final JWTUtil jwtUtil;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> registerUser(@RequestPart("registerDTO") RegisterDTO registerDTO, @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        RegisterDTO.Res res = userService.register(registerDTO, image);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER, res));
    }

    @PostMapping("/kakao-code")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {

        Map<String, String> info = kakaoLoginService.getKakaoUserInfo(code);

        String kakaoId = info.get("kakaoId"); // kakaoId 추출
        String nickname = info.get("nickname"); // nickname 추출

        // 유저가 있는지 확인
        User user = kakaoLoginService.findUserByKakaoId(kakaoId);

        // 유저가 없으면 kakao Id만 전달
        if (user == null) {
            NotUserDTO notUserDTO = new NotUserDTO(kakaoId, nickname);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(SuccessCode.NOT_USER, notUserDTO));
        }

        // CustomUserDetails 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();
        String userType = user.getUserType().toString();

        // JWT 토큰 생성 및 응답 헤더에 추가
        String accessToken = jwtUtil.createJwt("accessToken", user.getKakaoId(), role, userType, 86400000L);
        String refreshToken = jwtUtil.createJwt("refreshToken", user.getKakaoId(), role, userType, 86400000L);

        RefreshEntity refreshEntity = new RefreshEntity(refreshToken, customUserDetails.getUsername());
        refreshRedisRepository.save(refreshEntity);

        response.addHeader("accessToken", "Bearer " + accessToken);
        response.addHeader("refreshToken", "Bearer " + refreshToken);

        MyPageDTO.DetailRes loginResponseDTO = MyPageDTO.DetailRes.toDTO(user);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(SuccessCode.SUCCESS_LOGIN, loginResponseDTO);

        // SecurityContext에 인증 정보 설정
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);

        return ResponseEntity.ok().build(); // 빈 응답 반환
    }
}
