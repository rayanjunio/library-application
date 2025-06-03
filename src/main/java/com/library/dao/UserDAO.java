package com.library.dao;

import com.library.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class UserDAO implements PanacheRepository<User> {
  @Inject
  EntityManager entityManager;

  @Transactional
  public void save(User user) {
    entityManager.persist(user);
  }

  @Transactional
  public User merge(User user) {
    return entityManager.merge(user);
  }

  @Transactional
  public void delete(long id) {
    User user = this.findById(id);

    if(user != null) entityManager.remove(user);
  }

  public Optional<User> findByEmail(String email) {
    return find("email", email).firstResultOptional();
  }

  public User findById(long id) {
    return entityManager.find(User.class, id);
  }

  public Optional<User> findByCpf(String cpf) {
    return find("cpf", cpf).firstResultOptional();
  }
}
