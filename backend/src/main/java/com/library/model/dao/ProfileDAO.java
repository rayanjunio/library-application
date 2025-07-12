package com.library.model.dao;

import com.library.model.entity.Profile;
import com.library.model.enums.Role;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@RequestScoped
public class ProfileDAO implements PanacheRepository<Profile> {
  @Inject
  EntityManager entityManager;

  public void save(Profile profile) {
    entityManager.persist(profile);
  }

  public void delete(Profile profile) {
    entityManager.remove(profile);
  }

  public Profile findByRole(String roleName) {
    return entityManager.createQuery(
            "SELECT p FROM Profile p WHERE p.role = :role", Profile.class)
            .setParameter("role", Enum.valueOf(Role.class, roleName))
            .getResultStream()
            .findFirst()
            .orElse(null);
  }
}
