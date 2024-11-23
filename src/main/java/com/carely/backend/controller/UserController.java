package com.carely.backend.controller;

import com.carely.backend.code.ErrorCode;
import com.carely.backend.code.SuccessCode;
import com.carely.backend.controller.docs.UserAPI;
import com.carely.backend.domain.RefreshEntity;
import com.carely.backend.domain.User;
import com.carely.backend.domain.enums.UserType;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.*;
import com.carely.backend.exception.UserMustNotCaregiverException;
import com.carely.backend.jwt.JWTUtil;
import com.carely.backend.repository.RefreshRepository;
import com.carely.backend.service.UserService;
import com.carely.backend.service.kakao.KakaoLoginService;
import com.carely.backend.util.TokenErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {
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

    @GetMapping("/kakao-code")
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

    @GetMapping("/mypage")
    public ResponseEntity<ResponseDTO> getMypage() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        MyPageDTO.DetailRes res = userService.getMypage(kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, res));
    }

    @GetMapping("/user-info/detail/{userId}")
    public ResponseEntity<ResponseDTO> getDetailUseInfo(@PathVariable("userId") Long userId) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        MyPageDTO.DetailRes res = userService.getDetailUserInfo(userId, kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, res));
    }

    @GetMapping("/verify-authentication")
    public ResponseEntity<ResponseDTO> verifyAuthentication() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO.Verification res = userService.verifyAuthentication(kakaoId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_LOCATION_VERIFICATION.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_LOCATION_VERIFICATION, res));
    }

    @PostMapping("/verify-authentication")
    public ResponseEntity<ResponseDTO> verifyAuthenticationPost(@RequestBody() AddressDTO addressDTO) {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO.VerificationAddress res = userService.verifyAuthenticationPost(kakaoId, addressDTO);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_LOCATION_VERIFICATION.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_LOCATION_VERIFICATION, res));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 헤더에서 refresh 키에 담긴 토큰을 꺼냄
        String refreshToken = request.getHeader("refresh");

        if (refreshToken == null) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return null;
        }

        try {
            if (jwtUtil.isExpired(refreshToken)) {
                TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return null;
            }

            String type = jwtUtil.getType(refreshToken);

            if (!type.equals("refreshToken")) {
                TokenErrorResponse.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
                return null;
            }

            // DB에 저장되어 있는지 확인
            Optional<RefreshEntity> isExist = refreshRedisRepository.findById(refreshToken);
            if (isExist.isEmpty()) {
                TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return null;
            }

            String username = jwtUtil.getUsername(refreshToken);
            String role = jwtUtil.getRole(refreshToken);
            String userType = jwtUtil.getUserType(refreshToken);

            // 새로운 Access token과 refreshToken 생성
            String newAccessToken = jwtUtil.createJwt("accessToken", username, role, userType, 600000L);
            String newRefreshToken = jwtUtil.createJwt("refreshToken", username, role, userType, 600000L);

            refreshRedisRepository.deleteById(refreshToken);
            addRefreshEntity(newRefreshToken, username);

            response.setHeader("accessToken", "Bearer " + newAccessToken);
            response.setHeader("refreshToken", "Bearer " + newRefreshToken);

            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ResponseDTO<>(SuccessCode.SUCCESS_REISSUE, null));

        } catch (ExpiredJwtException e) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return null;
        } catch (Exception e) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
            return null;
        }
    }


    // 회원 탈퇴
    @DeleteMapping("/delete-user")
    public ResponseEntity<ResponseDTO> deleteUser() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUser(kakaoId);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_DELETE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_USER, null));

    }

    @GetMapping("/caregiver/recommend-users")
    public ResponseEntity<ResponseDTO>  recommendUsers() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userService.findUserByKakaoId(kakaoId);

        if(!currentUser.getUserType().equals(UserType.CAREGIVER))
            throw new UserMustNotCaregiverException("");

        List<RecommandUserDTO.Res> res = userService.recommendUsers(currentUser);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, res));
    }

    @GetMapping("/non-caregiver/recommend-users")
    public ResponseEntity<ResponseDTO>  recommandCaregiver() {
        String kakaoId = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userService.findUserByKakaoId(kakaoId);

        if(currentUser.getUserType().equals(UserType.CAREGIVER))
            throw new UserMustNotCaregiverException("");

        List<RecommandUserDTO.Res> res = userService.recommendCaregivers(currentUser);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, res));
    }

    private void addRefreshEntity(String refresh, String username) {
        RefreshEntity refreshEntity = new RefreshEntity(refresh, username);
        refreshRedisRepository.save(refreshEntity);
    }
}
