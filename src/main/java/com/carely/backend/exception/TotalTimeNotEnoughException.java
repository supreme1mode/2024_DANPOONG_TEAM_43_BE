package com.carely.backend.exception;

public class TotalTimeNotEnoughException extends RuntimeException{
    public TotalTimeNotEnoughException(String message) {
        super(message);
    }
}
