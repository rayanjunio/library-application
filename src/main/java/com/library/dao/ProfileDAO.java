package com.library.dao;

import com.library.model.Profile;
import com.library.model.Role;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class ProfileDAO implements PanacheRepository<Profile> {
  @Inject
  EntityManager entityManager;

  @Transactional
  public void save(Profile profile) {
    entityManager.persist(profile);
  }

  @Transactional
  public Profile merge(Profile profile) {
    return entityManager.merge(profile);
  }

  @Transactional
  public void delete(Profile profile) {
    entityManager.remove(profile);
  }

  public Optional<Profile> findByRole(String roleName) {
    return entityManager.createQuery(
            "SELECT p FROM Profile p WHERE p.role = :role", Profile.class)
            .setParameter("role", Enum.valueOf(Role.class, roleName))
            .getResultStream()
            .findFirst();
  }
}
