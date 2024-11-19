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
