package com.library.model.dao;

import com.library.model.entity.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@RequestScoped
public class LogDAO {
  @Inject
  EntityManager entityManager;

  public void save(Log log) {
    entityManager.persist(log);
  }
}
