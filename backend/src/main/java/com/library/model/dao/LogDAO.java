package com.library.model.dao;

import com.library.model.entity.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class LogDAO {
  @Inject
  EntityManager entityManager;

  @Transactional
  public void save(Log log) {
    entityManager.persist(log);
  }
}
