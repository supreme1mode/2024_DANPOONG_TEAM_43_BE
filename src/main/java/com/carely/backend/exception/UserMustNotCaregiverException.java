package com.carely.backend.exception;

public class UserMustNotCaregiverException extends RuntimeException {
    public UserMustNotCaregiverException(String message) {
        super(message);
    }
}
