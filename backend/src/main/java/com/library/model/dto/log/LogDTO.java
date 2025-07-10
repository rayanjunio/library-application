package com.library.model.dto.log;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class LogDTO {
  private String action;
  private long userId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime timestamp;

  public LogDTO(String action, long userId, LocalDateTime timestamp) {
    this.action = action;
    this.userId = userId;
    this.timestamp = timestamp;
  }

  public String getAction() {
    return action;
  }

  public long getUserId() {
    return userId;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
