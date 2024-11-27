package com.carely.backend.exception;

public class NotValidUserTypeException extends RuntimeException{
    public NotValidUserTypeException(String message) {
        super(message);
    }
}
