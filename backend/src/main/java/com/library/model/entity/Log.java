package com.library.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.library.model.dto.log.LogDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class Log {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotBlank
  @Column
  private String action;

  @NotNull
  @Column
  private long userId;

  @NotNull
  @Column
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime timestamp;

  public Log() {}

  public Log(LogDTO logDTO) {
    this.action = logDTO.getAction();
    this.userId = logDTO.getUserId();
    this.timestamp = logDTO.getTimestamp();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

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