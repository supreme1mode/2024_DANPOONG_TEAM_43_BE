package com.carely.backend.exception;

public class AlreadyInGroupException extends RuntimeException {
  public AlreadyInGroupException(String message) {
    super(message);
  }
}
