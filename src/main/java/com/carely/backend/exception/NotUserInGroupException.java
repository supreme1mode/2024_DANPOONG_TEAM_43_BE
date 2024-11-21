package com.carely.backend.exception;

public class NotUserInGroupException extends RuntimeException {
  public NotUserInGroupException(String message) {
    super(message);
  }
}
