package com.library.model.dto.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.enterprise.context.RequestScoped;

import java.time.LocalDateTime;

@RequestScoped
public class RequestLogContextDTO {
  private String action;
  private long userId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime timestamp;

  public RequestLogContextDTO() {}

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
