package com.carely.backend.exception;

public class NoCertificateUserException extends RuntimeException{
    public NoCertificateUserException(String message) {
        super(message);
    }
}
