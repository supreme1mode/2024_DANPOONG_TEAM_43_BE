package com.carely.backend.exception;

import com.carely.backend.code.ErrorCode;
import com.carely.backend.code.SuccessCode;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandleException {
    @ExceptionHandler(DuplicateUsernameException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDuplicateUsernameException(final DuplicateUsernameException e) {
        return ResponseEntity
                .status(ErrorCode.DUPLICATE_USERNAME.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.DUPLICATE_USERNAME));
    }

    @ExceptionHandler(KakaoException.class)
    protected ResponseEntity<ErrorResponseDTO> handleKakaoException(final KakaoException e) {
        return ResponseEntity
                .status(ErrorCode.KAKAO_EXCEPTION.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.KAKAO_EXCEPTION));
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(final UserNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.USER_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.USER_NOT_FOUND));
    }

    @ExceptionHandler(VolunteerNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleVolunteerNotFoundException(final VolunteerNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.VOLUNTEER_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.VOLUNTEER_NOT_FOUND));
    }

    @ExceptionHandler(NoCertificateUserException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNoCertificateUserException(final NoCertificateUserException e) {
        return ResponseEntity
                .status(ErrorCode.HAS_NOT_CERTIFICATE.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.HAS_NOT_CERTIFICATE));
    }

    @ExceptionHandler(GuestBookNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleGuestBookNotFoundException(final GuestBookNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.GUESTBOOK_NOT_FOUNT.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.GUESTBOOK_NOT_FOUNT));
    }

    @ExceptionHandler(ObjectNullException.class)
    protected ResponseEntity<ErrorResponseDTO> handleObjectNullException(final ObjectNullException e) {
        return ResponseEntity
                .status(ErrorCode.CACHE_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.CACHE_NOT_FOUND));
    }

    @ExceptionHandler(IdentityNotAcceptableException.class)
    protected ResponseEntity<ErrorResponseDTO> handleIdentityNotAcceptableException(final IdentityNotAcceptableException e) {
        return ResponseEntity
                .status(ErrorCode.NOT_IDENTITY_USER.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.NOT_IDENTITY_USER));
    }

    @ExceptionHandler(AlreadyHasCertificateException.class)
    protected ResponseEntity<ErrorResponseDTO> handleAlreadyHasCertificateException(final AlreadyHasCertificateException e) {
        return ResponseEntity
                .status(ErrorCode.ALREADY_HAS_CERTIFICATE.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.ALREADY_HAS_CERTIFICATE));
    }

    @ExceptionHandler(AlreadyExistsGuestBookException.class)
    protected ResponseEntity<ErrorResponseDTO> handleAlreadyExistsGuestBookException(final AlreadyExistsGuestBookException e) {
        return ResponseEntity
                .status(ErrorCode.ALREADY_EXISTS_GUESTBOOK.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.ALREADY_EXISTS_GUESTBOOK));
    }

    @ExceptionHandler(NoFileException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNoFileException(final NoFileException e) {
        return ResponseEntity
                .status(ErrorCode.NO_FILE.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.NO_FILE));
    }

    @ExceptionHandler(TotalTimeNotEnoughException.class)
    protected ResponseEntity<ErrorResponseDTO> handleTotalTimeNotEnoughException(final TotalTimeNotEnoughException e) {
        return ResponseEntity
                .status(ErrorCode.TOTAL_TIME_NOT_ENOUGH.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.TOTAL_TIME_NOT_ENOUGH));
    }

    @ExceptionHandler(CertificateNotValidException.class)
    protected ResponseEntity<ErrorResponseDTO> handleCertificationNotValidException(final CertificateNotValidException e) {
        return ResponseEntity
                .status(ErrorCode.CERTIFICATE_FAIL.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.CERTIFICATE_FAIL));
    }


    @ExceptionHandler(ChatMessageNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleChatMessageNotFoundException(final ChatMessageNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.CHAT_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.CHAT_NOT_FOUND));
    }

    @ExceptionHandler(ListEmptyException.class)
    protected ResponseEntity<ResponseDTO<?>> handleListEmptyException(final ListEmptyException e) {
        return ResponseEntity
                .status(SuccessCode.SUCCESS_BUT_LIST_EMPTY.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_BUT_LIST_EMPTY, null));
    }

    @ExceptionHandler(NotValidUserTypeException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNotValidUserTypeException(final NotValidUserTypeException e) {
        return ResponseEntity
                .status(ErrorCode.USER_TYPE_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.USER_TYPE_NOT_FOUND));
    }

    @ExceptionHandler(NotValidAddressException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNotValidAddressException(final NotValidAddressException e) {
        return ResponseEntity
                .status(ErrorCode.NOT_VALID_ADDRESS.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.NOT_VALID_ADDRESS));
    }

    @ExceptionHandler(KakaoIdNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleKakaoIdNotFoundException(final KakaoIdNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.USER_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.USER_NOT_FOUND));
    }

    @ExceptionHandler(UserNotMatchException.class)
    protected ResponseEntity<ErrorResponseDTO> handleUserNotMatchException(final UserNotMatchException e) {
        return ResponseEntity
                .status(ErrorCode.USER_NOT_MATCH.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.USER_NOT_MATCH));
    }

    @ExceptionHandler(UserMustNotCaregiverException.class)
    protected ResponseEntity<ErrorResponseDTO> handleUserMustNotCaregiverException(final UserMustNotCaregiverException e) {
        return ResponseEntity
                .status(ErrorCode.USER_MUST_NOT_CAREGIVER.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.USER_MUST_NOT_CAREGIVER));
    }

    @ExceptionHandler(UserMustCaregiverException.class)
    protected ResponseEntity<ErrorResponseDTO> handleUserMustCaregiverException(final UserMustCaregiverException e) {
        return ResponseEntity
                .status(ErrorCode.USER_MUST_CAREGIVER.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.USER_MUST_CAREGIVER));
    }

    @ExceptionHandler(AlreadyExistsMemoException.class)
    protected ResponseEntity<ErrorResponseDTO> handleAlreadyExistsMemoException(final AlreadyExistsMemoException e) {
        return ResponseEntity
                .status(ErrorCode.ALREADY_EXISTS_MEMO.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.ALREADY_EXISTS_MEMO));
    }

    @ExceptionHandler(NotEligibleCaregiver.class)
    protected ResponseEntity<ErrorResponseDTO> handleNotEligibleCaregiver(final NotEligibleCaregiver e) {
        return ResponseEntity
                .status(ErrorCode.NOT_ELIGIBLE_CAREGIVER.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.NOT_ELIGIBLE_CAREGIVER));
    }

    @ExceptionHandler(AlreadyApprovedException.class)
    protected ResponseEntity<ErrorResponseDTO> handleAlreadyApprovedException(final AlreadyApprovedException e) {
        return ResponseEntity
                .status(ErrorCode.ALREADY_APPROVED.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.ALREADY_APPROVED));
    }

    @ExceptionHandler(NotMatchChatroomException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNotMatchChatroomException(final NotMatchChatroomException e) {
        return ResponseEntity
                .status(ErrorCode.NOT_MATCH_CHATROOM.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.NOT_MATCH_CHATROOM));
    }

    @ExceptionHandler(NewsNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNewsNotFoundException(final NewsNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.NEWS_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.NEWS_NOT_FOUND));
    }
    /**
     * 입력값 검증
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.BAD_REQUEST, errors));
    }

}
