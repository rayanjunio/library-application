package com.library.model.bo;

import com.library.model.dao.LogDAO;
import com.library.model.dto.log.LogDTO;
import com.library.model.entity.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class LogBO {

  @Inject
  LogDAO logDAO;

  @Transactional
  public void create(LogDTO logDTO) {
    logDAO.save(new Log(logDTO));
  }
}
