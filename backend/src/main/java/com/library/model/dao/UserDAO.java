package com.library.model.dao;

import com.library.model.entity.User;
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

  public Optional<User> findByIdWithLoans(long id) {
    return entityManager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.loans WHERE u.id = :id", User.class)
            .setParameter("id", id)
            .getResultStream()
            .findFirst();
  }

  public Optional<User> findByCpf(String cpf) {
    return find("cpf", cpf).firstResultOptional();
  }
}
