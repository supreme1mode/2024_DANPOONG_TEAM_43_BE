package com.carely.backend.exception;

public class GuestBookNotFoundException extends RuntimeException{
    public GuestBookNotFoundException(String message) {
        super(message);
    }
}
