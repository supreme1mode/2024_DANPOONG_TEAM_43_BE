package com.carely.backend.exception;

public class NotEligibleCaregiver extends RuntimeException {
    public NotEligibleCaregiver(String message) {
        super(message);
    }
}
