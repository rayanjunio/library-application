package com.library.model.dto.log;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class LogDTO {
  private String action;
  private long userId;
  private int status;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime timestamp;

  public LogDTO(String action, long userId, int status, LocalDateTime timestamp) {
    this.action = action;
    this.userId = userId;
    this.status = status;
    this.timestamp = timestamp;
  }

  public String getAction() {
    return action;
  }

  public long getUserId() {
    return userId;
  }

  public int getStatus() {
    return status;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
