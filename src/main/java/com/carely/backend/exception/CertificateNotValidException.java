package com.carely.backend.exception;

public class CertificateNotValidException extends RuntimeException{
    public CertificateNotValidException(String message) {
        super(message);
    }
}
