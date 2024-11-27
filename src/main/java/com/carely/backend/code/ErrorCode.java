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
    NOT_VALID_ADDRESS(HttpStatus.BAD_REQUEST, "잘못된 주소입니다."),
    NOT_WRITER(HttpStatus.BAD_REQUEST, "작성자가 아닙니다."),
    NOT_ELIGIBLE_CAREGIVER(HttpStatus.BAD_REQUEST, "약속 요청을 받은 대상자가 아닙나다."),
    NOT_MATCH_CHATROOM(HttpStatus.BAD_REQUEST, "채팅방에 해당하지 않는 채팅 메세지입니다. 채팅방 id를 다시 확인하세요."),

    /**
     * 401
     */
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "요청 헤더에 토큰이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    USER_MUST_CAREGIVER(HttpStatus.UNAUTHORIZED, "요청하는 유저가 간병인이어야 합니다."),
    USER_MUST_NOT_CAREGIVER(HttpStatus.UNAUTHORIZED, "요청하는 유저가 자원봉사자 혹은 요양보호사여야 합니다."),
    USER_NOT_MATCH(HttpStatus.UNAUTHORIZED, "자격증의 정보가 가입한 회원정보와 일치하지 않습니다."),

    /**
     * 404
     */
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 타입을 찾을 수 없습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터베이스에서 토큰을 찾을 수 없습니다."),
    VOLUNTEER_NOT_FOUND(HttpStatus.NOT_FOUND, "자원봉사 요청을 찾을 수 없습니다."),
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅 메세지를 찾을 수 없습니다."),
    NOT_IN_GROUP(HttpStatus.NOT_FOUND, "가입된 적이 없는 유저입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹을 찜하지 않은 유저입니다."),
    CERTIFICATE_FAIL(HttpStatus.NOT_FOUND, "자격증 인증에 실패했습니다."),
    HAS_NOT_CERTIFICATE(HttpStatus.NOT_FOUND, "자격증이 존재하지 않는 유저입니다."),

    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "소식을 찾을 수 없습니다."),

    /**
     *  406
     * */
    ALREADY_EXISTS_GUESTBOOK(HttpStatus.NOT_ACCEPTABLE, "이미 해당 활동에 대한 방명록이 존재합니다."),
    ALREADY_EXISTS_MEMO(HttpStatus.NOT_ACCEPTABLE, "이미 해당 활동에 대한 메모가 존재합니다."),
    ALREADY_APPROVED(HttpStatus.NOT_ACCEPTABLE, "이미 승인된 약속입니다."),
    ALREADY_HAS_CERTIFICATE(HttpStatus.NOT_ACCEPTABLE, "이미 자격증이 존재하여 발급할 수 없습니다."),
    TOTAL_TIME_NOT_ENOUGH(HttpStatus.NOT_ACCEPTABLE, "실습 시간이 충분하지 않아 자격증을 발급할 수 없습니다."),




    /**
     * 409
     */
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "중복된 카카오 아이디입니다."),
    ALREADY_IN_GROUP(HttpStatus.CONFLICT, "이미 가입되어 있는 유저입니다."),
    ALREADY_LIKE_GROUP(HttpStatus.CONFLICT, "이미 찜한 그룹입니다."),

    /**
     * 500
     */
    KAKAO_EXCEPTION(HttpStatus.OK, "카카오 API 사용 중 에러가 발생했습니다."),

    ;
    private final HttpStatus status;
    private final String message;
}
