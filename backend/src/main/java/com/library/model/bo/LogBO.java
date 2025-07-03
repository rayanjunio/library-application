package com.library.model.bo;

import com.library.model.dao.LogDAO;
import com.library.model.dto.log.LogDTO;
import com.library.model.entity.Log;
import com.library.model.mapper.LogMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class LogBO {

  @Inject
  LogDAO logDAO;

  @Inject
  LogMapper logMapper;

  public void create(LogDTO logDTO) {
    Log log = logMapper.toEntity(logDTO);
    logDAO.save(log);
  }
}
