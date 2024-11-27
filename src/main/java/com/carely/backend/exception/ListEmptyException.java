package com.carely.backend.exception;

public class ListEmptyException extends RuntimeException{
    public ListEmptyException(String message) {
        super(message);
    }
}
