package com.library.model.mapper;

import com.library.model.dto.log.LogDTO;
import com.library.model.entity.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogMapper {

  public Log toEntity(LogDTO logDTO) {
    Log log = new Log();
    log.setAction(logDTO.getAction());
    log.setUserId(logDTO.getUserId());
    log.setStatus(logDTO.getStatus());
    log.setTimestamp(logDTO.getTimestamp());
    return log;
  }

  public LogDTO toDTO(Log log) {
    return new LogDTO(log.getAction(), log.getUserId(), log.getStatus(), log.getTimestamp());
  }
}
