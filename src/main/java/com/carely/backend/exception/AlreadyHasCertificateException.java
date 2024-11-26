package com.carely.backend.exception;

public class AlreadyHasCertificateException extends RuntimeException{
    public AlreadyHasCertificateException(String message){
        super(message);
    }
}
