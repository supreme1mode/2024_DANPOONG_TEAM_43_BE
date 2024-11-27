package com.carely.backend.exception;

public class NewsNotFoundException extends RuntimeException {
  public NewsNotFoundException(String message) {
    super(message);
  }
}
