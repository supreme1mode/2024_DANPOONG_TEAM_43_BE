package com.carely.backend.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {
    /**
     * User
     */
    SUCCESS_REISSUE(HttpStatus.OK, "토큰 재발급을 성공했습니다. 헤더 토큰을 확인하세요."),
    SUCCESS_REGISTER(HttpStatus.OK, "회원가입을 성공했습니다."),
    SUCCESS_LOGIN(HttpStatus.OK, "로그인을 성공했습니다. 헤더 토큰을 확인하세요."),
    SUCCESS_LOGOUT(HttpStatus.OK, "성공적으로 로그아웃했습니다."),

    SUCCESS_RETRIEVE_USER(HttpStatus.OK, "유저 정보 조회를 성공했습니다."),
    SUCCESS_RETRIEVE_LOCATION_VERIFICATION(HttpStatus.OK, "성공적으로 위치 인증 여부를 조회했습니다."),
    SUCCESS_LOCATION_VERIFICATION(HttpStatus.OK, "성공적으로 위치 인증했습니다."),
    SUCCESS_DELETE_USER(HttpStatus.OK, "성공적으로 유저가 삭제되었습니다."),


    /**
     * 로그인
     */
    NOT_USER(HttpStatus.NOT_FOUND, "유저가 아닙니다. 회원가입을 진행해주세요."),

    /**
     * Chat
     */
    SUCCESS_EXIST_CHATROOM(HttpStatus.OK, "이미 해당 username으로 채팅방이 존재합니다."),
    SUCCESS_CREATE_CHATROOM(HttpStatus.CREATED, "채팅방이 성공적으로 생성되었습니다."),
    SUCCESS_FIND_CHATROOM(HttpStatus.OK, "모든 채팅방을 조회했습니다."),



    /**
     * 그룹
     */


    /**
     * volunteer
     */
    SUCCESS_CREATE_VOLUNTEER(HttpStatus.OK, "자원봉사 요청을 성공적으로 생성했습니다."),
    SUCCESS_APPROVAL(HttpStatus.OK, "자원봉사 요청을 성공적으로 승인했습니다."),
    SUCCESS_RETRIEVE_VOLUNTEER(HttpStatus.OK, "자원봉사 요청을 성공적으로 조회했습니다."),

    /**
     * Memo
     */
    ;
    private final HttpStatus status;
    private final String message;
}