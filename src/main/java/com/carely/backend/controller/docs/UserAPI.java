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

import java.io.IOException;

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
    ResponseEntity<ResponseDTO> registerUser(@RequestPart("registerDTO") RegisterDTO registerDTO, @RequestPart MultipartFile file) throws IOException;

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
    ResponseEntity<?> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "현재 로그인한 유저 정보 조회에 성공한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_USER\", \"message\": \"유저 정보 조회를 성공했습니다.\", \"data\": { \"userId\": 90, \"username\": \"김은서\", \"phoneNum\": \"01082302512\", \"city\": \"성남시 분당구\", \"address\": \"경기도 성남시 분당구 양현로 353\", \"detailAddress\": \"\", \"locationAuthentication\": true, \"userType\": \"VOLUNTEER\", \"shareLocation\": true, \"latitude\": 37.4147451179385, \"longitude\": 127.122162329923, \"age\": 22, \"certificateImage\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/certificate/image.png\", \"talk\": \"상급\", \"eat\": \"상급\", \"toilet\": \"상급\", \"bath\": \"상급\", \"walk\": \"중급\", \"story\": \"예비 요양보호사로서 책임을 가지고 임하고 있습니다.\", \"guestbookDTOS\": null }}"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = "{ \"status\": 401, \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = "{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_MISSING", value = "{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 accessToken으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"error\": \"NOT_FOUND\", \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"존재하지 않는 accessToken입니다.\" }")
                            }))
    })
    @Operation(summary = "현재 로그인한 유저 정보 조회하기", description = "현재 로그인한 유저 정보를 조회합니다.")
    public ResponseEntity<ResponseDTO> getMypage();

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "현재 로그인한 유저 정보 조회에 성공한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_USER\", \"message\": \"유저 정보 조회를 성공했습니다.\", \"data\": { \"userId\": 40, \"username\": \"고은별\", \"phoneNum\": \"010-4567-8901\", \"city\": \"성남시 분당구\", \"address\": \"경기도 성남시 분당구 대장동 산13-2\", \"detailAddress\": \"300호\", \"locationAuthentication\": true, \"userType\": \"VOLUNTEER\", \"shareLocation\": true, \"latitude\": 0, \"longitude\": 0, \"age\": 56, \"certificateImage\": \"https://groomcaregiver.s3.ap-northeast-2.amazonaws.com/certificate/image.png\", \"talk\": \"상급\", \"eat\": \"하급\", \"toilet\": \"하급\", \"bath\": \"중급\", \"walk\": \"중급\", \"story\": \"요양원에서 봉사하며 어르신들에게 필요한 정서적 지원을 하고 있습니다.\", \"guestbookDTOS\": null, \"togetherTime\": 0 }}"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = "{ \"status\": 401, \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = "{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_MISSING", value = "{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 accessToken으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"error\": \"NOT_FOUND\", \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"존재하지 않는 accessToken입니다.\" }")
                            }))
    })
    @Operation(summary = "유저 상세 정보 조회하기", description = "유저의 상세 정보를 조회합니다.")
    ResponseEntity<ResponseDTO> getDetailUseInfo(@PathVariable("userId") Long userId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 위치 인증 여부를 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_LOCATION_VERIFICATION\", \"message\": \"성공적으로 위치 인증 여부를 조회했습니다.\", \"data\": { \"userId\": 90, \"username\": \"김은서\", \"userType\": \"VOLUNTEER\", \"locationAuthentication\": true }}"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = "{ \"status\": 401, \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = "{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_MISSING", value = "{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 accessToken으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"error\": \"NOT_FOUND\", \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"존재하지 않는 accessToken입니다.\" }")
                            }))
    })
    @Operation(summary = "현재 로그인한 유저 위치 인증 여부 조회하기", description = "현재 로그인한 유저의 위치 인증 여부를 조회합니다.")    ResponseEntity<ResponseDTO> verifyAuthentication();

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 위치 인증한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_LOCATION_VERIFICATION\", \"message\": \"성공적으로 위치 인증했습니다.\", \"data\": { \"userId\": 90, \"username\": \"김은서\", \"userType\": \"VOLUNTEER\", \"locationAuthentication\": true, \"address\": \"경기도 성남시 분당구 양현로 353\", \"detailAddress\": \"\" }}"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = "{ \"status\": 401, \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = "{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_MISSING", value = "{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 accessToken으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"error\": \"NOT_FOUND\", \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"존재하지 않는 accessToken입니다.\" }")
                            }))
    })
    @Operation(summary = "주소를 전달해 위치 인증하기", description = "주소를 전달해 위치 인증을 진행합니다.")
    public ResponseEntity<ResponseDTO> verifyAuthenticationPost(@RequestBody AddressDTO addressDTO);

    @Operation(summary = "유저 삭제하기", description = "유저를 삭제합니다. (회원 탈퇴로 나중에 수정하기)gi")
    ResponseEntity<ResponseDTO> deleteUser(@PathVariable String kakao_id) ;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 간병인 추천을 받은 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_USER\", \"message\": \"유저 정보 조회를 성공했습니다.\", \"data\": [ { \"userId\": 91, \"username\": \"이규민\", \"userType\": \"CAREGIVER\", \"timeTogether\": 18, \"address\": \"서울특별시 도봉구 우이천로 394\", \"latitude\": 37.3426052729963, \"longitude\": 127.09922038235, \"km\": 6.163155744795938, \"talk\": \"수월\", \"eat\": \"서투름\", \"toilet\": \"서투름\", \"bath\": \"서투름\", \"walk\": \"서투름\" }, { \"userId\": 61, \"username\": \"윤지인\", \"userType\": \"CAREGIVER\", \"timeTogether\": 0, \"address\": \"경기도 성남시 분당구 동원로1번길 21\", \"latitude\": 37.3902, \"longitude\": 127.1108, \"km\": 0.8939377003160186, \"talk\": \"보통\", \"eat\": \"보통\", \"toilet\": \"보통\", \"bath\": \"서투름\", \"walk\": \"보통\" } ] }"))),

            @ApiResponse(responseCode = "401", description = "요청한 유저가 간병인이 아닌 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 401, \"error\": \"UNAUTHORIZED\", \"code\": \"USER_MUST_NOT_CAREGIVER\", \"message\": \"요청하는 유저가 간병인이어야 합니다.\" }"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = "{ \"status\": 401, \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = "{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_MISSING", value = "{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }")
                            }))
    })
    @Operation(summary = "간병인 유저 추천받기", description = "자원봉사자, 혹은 요양보호사가 간병인을 추천받습니다.")
    public ResponseEntity<ResponseDTO> recommandCaregiver();

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 유저 추천을 받은 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_USER\", \"message\": \"유저 정보 조회를 성공했습니다.\", \"data\": [ { \"userId\": 91, \"username\": \"이규민\", \"userType\": \"CARE_WORKER\", \"timeTogether\": 18, \"address\": \"서울특별시 도봉구 우이천로 394\", \"latitude\": 37.3426052729963, \"longitude\": 127.09922038235, \"km\": 6.163155744795938, \"talk\": \"수월\", \"eat\": \"서투름\", \"toilet\": \"서투름\", \"bath\": \"서투름\", \"walk\": \"서투름\" }, { \"userId\": 61, \"username\": \"윤지인\", \"userType\": \"CARE_WORKER\", \"timeTogether\": 0, \"address\": \"경기도 성남시 분당구 동원로1번길 21\", \"latitude\": 37.3902, \"longitude\": 127.1108, \"km\": 0.8939377003160186, \"talk\": \"보통\", \"eat\": \"보통\", \"toilet\": \"보통\", \"bath\": \"서투름\", \"walk\": \"보통\" } ] }"))),

            @ApiResponse(responseCode = "401", description = "요청한 유저가 자원봉사자 혹은 요양보호사가 아닌 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 401, \"error\": \"UNAUTHORIZED\", \"code\": \"USER_MUST_NOT_CAREGIVER\", \"message\": \"요청하는 유저가 자원봉사자 혹은 요양보호사여야 합니다.\" }"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = "{ \"status\": 401, \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = "{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_MISSING", value = "{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }")
                            }))
    })
    @Operation(summary = "자원봉사자, 간병인 유저 추천받기", description = "간병인이 자원봉사자, 혹은 요양보호사를 추천받습니다.")
    public ResponseEntity<ResponseDTO> recommendUsers();

    }
