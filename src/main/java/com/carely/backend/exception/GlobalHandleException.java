package com.carely.backend.exception;

import com.carely.backend.code.ErrorCode;
import com.carely.backend.dto.response.ErrorResponseDTO;
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

    @ExceptionHandler(ChatMessageNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleChatMessageNotFoundException(final ChatMessageNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.CHAT_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.CHAT_NOT_FOUND));
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

    @ExceptionHandler(GroupNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleGroupNotFoundException(final GroupNotFoundException e) {
        return ResponseEntity
                .status(ErrorCode.GROUP_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.GROUP_NOT_FOUND));
    }

    @ExceptionHandler(AlreadyInGroupException.class)
    protected ResponseEntity<ErrorResponseDTO> handleAlreadyInGroupException(final AlreadyInGroupException e) {
        return ResponseEntity
                .status(ErrorCode.ALREADY_IN_GROUP.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.ALREADY_IN_GROUP));
    }

    @ExceptionHandler(NotUserInGroupException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNotUserInGroupException(final NotUserInGroupException e) {
        return ResponseEntity
                .status(ErrorCode.NOT_IN_GROUP.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.NOT_IN_GROUP));
    }

    @ExceptionHandler(NotLikeGroupUserException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNotLikeGroupUserException(final NotLikeGroupUserException e) {
        return ResponseEntity
                .status(ErrorCode.LIKE_NOT_FOUND.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.LIKE_NOT_FOUND));
    }

    @ExceptionHandler(AlreadyLikeGroupException.class)
    protected ResponseEntity<ErrorResponseDTO> handleAlreadyLikeGroupException(final AlreadyLikeGroupException e) {
        return ResponseEntity
                .status(ErrorCode.ALREADY_LIKE_GROUP.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.ALREADY_LIKE_GROUP));
    }

    @ExceptionHandler(NotWriterException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNotWriterException(final NotWriterException e) {
        return ResponseEntity
                .status(ErrorCode.NOT_WRITER.getStatus().value())
                .body(new ErrorResponseDTO(ErrorCode.NOT_WRITER));
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
