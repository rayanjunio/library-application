package com.library.model.dao;

import com.library.model.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class UserDAO implements PanacheRepository<User> {
  @Inject
  EntityManager entityManager;

  public void save(User user) {
    entityManager.persist(user);
  }

  public User merge(User user) {
    return entityManager.merge(user);
  }

  public void delete(User user) {
    this.entityManager.remove(user);
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

  public List<User> findAllUsers(int page, int size) {
    return entityManager.createQuery("SELECT u FROM User u", User.class)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .getResultList();
  }

  public long countAllUsers() {
    return entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class)
            .getSingleResult();
  }
}
