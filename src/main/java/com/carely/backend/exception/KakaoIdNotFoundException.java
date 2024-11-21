package com.carely.backend.exception;

public class KakaoIdNotFoundException extends RuntimeException {
    public KakaoIdNotFoundException(String message) {
        super(message);
    }
}
