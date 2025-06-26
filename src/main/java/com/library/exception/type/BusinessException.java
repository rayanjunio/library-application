package com.library.exception.type;

public class BusinessException extends RuntimeException {
  private final int status;

  public BusinessException(String message, int status) {
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return this.status;
  }
}
