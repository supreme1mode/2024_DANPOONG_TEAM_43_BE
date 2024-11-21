package com.carely.backend.exception;

public class AlreadyExistsGuestBookException extends RuntimeException{
    public AlreadyExistsGuestBookException(String message) {
        super(message);
    }
}
