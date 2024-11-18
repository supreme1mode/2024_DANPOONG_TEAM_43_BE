package com.carely.backend.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    /**
     * 400
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    /**
     * 401
     */
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "요청 헤더에 토큰이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),

    /**
     * 404
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터베이스에서 토큰을 찾을 수 없습니다."),

    /**
     * 409
     */
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "중복된 카카오 아이디입니다."),
    ALREADY_IN_GROUP(HttpStatus.CONFLICT, "이미 가입되어 있는 유저입니다."),

    /**
     * 500
     */
    KAKAO_EXCEPTION(HttpStatus.OK, "카카오 API 사용 중 에러가 발생했습니다."),

    ;
    private final HttpStatus status;
    private final String message;
}
