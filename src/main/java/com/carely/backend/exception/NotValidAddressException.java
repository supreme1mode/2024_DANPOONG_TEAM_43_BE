package com.carely.backend.exception;

public class NotValidAddressException extends RuntimeException {
    public NotValidAddressException(String message) {
        super(message);
    }
}
