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
     * Memo
     */
    SUCCESS_CREATE_MEMO(HttpStatus.OK, "메모를 성공적으로 작성했습니다."),
    SUCCESS_RETRIEVE_MEMO(HttpStatus.OK, "성공적으로 메모를 조회했습니다."),

    /**
     * 그룹
     */
    SUCCESS_CREATE_GROUP(HttpStatus.CREATED, "그룹을 성공적으로 생성했습니다."),
    SUCCESS_REGISTER_GROUP(HttpStatus.OK, "그룹을 성공적으로 조회했습니다."),
    SUCCESS_JOIN_GROUP(HttpStatus.OK, "그룹에 성공적으로 가입되었습니다."),
    SUCCESS_LEAVE_GROUP(HttpStatus.OK, "그룹 가입이 성공적으로 취소되었습니다."),
    SUCCESS_ADD_LIKE(HttpStatus.OK, "그룹을 성공적으로 찜했습니다."),
    SUCCESS_REMOVE_LIKE(HttpStatus.OK, "그룹 찜을 성공적으로 취소했습니다."),
    SUCCESS_DELETE_GROUP(HttpStatus.OK, "그룹을 성공적으로 삭제했습니다."),



    /**
     * volunteer
     */
    SUCCESS_CREATE_VOLUNTEER(HttpStatus.OK, "자원봉사 요청을 성공적으로 생성했습니다."),
    SUCCESS_APPROVAL(HttpStatus.OK, "자원봉사 요청을 성공적으로 승인했습니다."),
    SUCCESS_RETRIEVE_VOLUNTEER(HttpStatus.OK, "자원봉사 요청을 성공적으로 조회했습니다."),

    /**
     * Memo
     */


    /*
     * GuestBook
     *
     * */

    SUCCESS_CREATE_GUESTBOOK(HttpStatus.OK, "방명록을 성공적으로 생성했습니다."),
    //SUCCESS_APPROVAL(HttpStatus.OK, "자원봉사 요청을 성공적으로 승인했습니다."),
    SUCCESS_RETRIEVE_GUESTBOOK(HttpStatus.OK, "방명록을 성공적으로 조회했습니다."),
    SUCCESS_DELETE_GUESTBOOK(HttpStatus.OK, "방명록을 성공적으로 삭제했습니다."),

    /**
     *
     * Certificate
     * */
    SUCCESS_ISSUE_CERTIFICATE(HttpStatus.OK, "자격증을 성공적으로 발급하였습니다."),
    SUCCESS_RETRIEVE_VOLUNTEER_LIST(HttpStatus.OK, "봉사 활동 목록을 성공적으로 조회하였습니다."),
    SUCCESS_RETRIEVE_CARE_WORKER_LIST(HttpStatus.OK, "요양보호 활동 목록을 성공적으로 조회하였습니다."),


    /**
     * OCR
     * */
    SUCCESS_OCR(HttpStatus.OK, "성공적으로 서류 검출을 진행했습니다."),


    ;
    private final HttpStatus status;
    private final String message;
}