package com.carely.backend.controller.docs;

import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.user.AddressDTO;
import com.carely.backend.dto.user.RegisterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Map;

public interface UserAPI {
    @Operation(summary = "회원가입하기", description = "회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입을 성공했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 200, \"code\": \"SUCCESS_REGISTER\", \"message\": \"회원가입을 성공했습니다.\", \"data\": { \"kakaoId\": \"377747839\", \"userType\": \"VOLUNTEER\", \"username\": \"김은서\", \"age\": 22, \"phoneNum\": \"010-8230-2512\", \"address\": \"경기도 과천시 과천동 376-17\", \"detailAddress\": \"202호\", \"locationAuthentication\": true } }")
                    )
            ),
            @ApiResponse(responseCode = "406", description = "요양보호사이지만 이미지 파일이 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 406, \"error\": \"CONFLICT\", \"code\": \"NO_FILE\", \"message\": \"요양보호사로 회원가입 시 자격증 사진이 필요합니다.\" }"))
            ),

            @ApiResponse(responseCode = "409", description = "데이베이스에 존재하는 회원이 회원가입을 진행할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 409, \"error\": \"CONFLICT\", \"code\": \"DUPLICATE_USERNAME\", \"message\": \"중복된 유저 이름입니다.\" }"))
            ),

    })
    public ResponseEntity<ResponseDTO> registerUser(@RequestPart("registerDTO") RegisterDTO registerDTO, @RequestPart MultipartFile file) throws IOException;

    @Operation(summary = "access token 재발급하기",
            parameters = {
                    @Parameter(name = "refresh", description = "Refresh token", required = true, in = ParameterIn.HEADER, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            },
            description = "refresh Token을 입력하여 access Token을 재발급 받습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급을 성공했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_REISSUE\", \"message\": \"토큰 재발급을 성공했습니다. 헤더 토큰을 확인하세요.\", \"data\": null }"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = "{ \"status\": 401, \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = "{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_MISSING", value = "{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 access Token으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }"),
                            })),
    })
    ResponseEntity<ResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException;

    @Operation(summary = "카카오 로그인", description = "카카오 code를 이용한 로그인")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException;

    @Operation(summary = "현재 로그인한 유저 정보 조회하기", description = "현재 로그인한 유저 정보를 조회합니다.")
    public ResponseEntity<ResponseDTO> getMypage();

    @Operation(summary = "유저 상세 정보 조회하기", description = "유저의 상세 정보를 조회합니다.")
    public ResponseEntity<ResponseDTO> getDetailUseInfo(@PathVariable("userId") Long userId);

    @Operation(summary = "위치 인증 여부 확인하기", description = "사용자가 위치를 인증했는지 확인합니다.")
    public ResponseEntity<ResponseDTO> verifyAuthentication();

    @Operation(summary = "주소를 전달해 위치 인증하기", description = "주소를 전달해 위치 인증을 진행합니다.")
    public ResponseEntity<ResponseDTO> verifyAuthenticationPost(@RequestBody() AddressDTO addressDTO);

    @Operation(summary = "유저 삭제하기", description = "유저를 삭제합니다. (회원 탈퇴로 나중에 수정하기)gi")
    public ResponseEntity<ResponseDTO> deleteUser();

}
