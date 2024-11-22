package com.carely.backend.exception;

public class AlreadyExistsMemoException extends RuntimeException{
    public AlreadyExistsMemoException(String message) {
        super(message);
    }
}
