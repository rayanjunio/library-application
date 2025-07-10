package com.library.model.dao;

import com.library.model.entity.Loan;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@RequestScoped
public class LoanDAO {
  @Inject
  EntityManager entityManager;

  public void save(Loan loan) {
    entityManager.persist(loan);
  }

  public Loan merge(Loan loan) {
    return entityManager.merge(loan);
  }

  public void delete(Loan loan) {
    this.entityManager.remove(loan);
  }

  public Loan findById(long id) {
    return entityManager.find(Loan.class, id);
  }

  public List<Loan> findAllFromUser(long userId) {
    return entityManager.createQuery("SELECT l FROM Loan l WHERE l.user.id = :userId ", Loan.class)
            .setParameter("userId", userId)
            .getResultList();
  }

  public long countUserPendingLoans(long userId) {
    return entityManager.createQuery("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.actualReturnDate IS NULL", Long.class)
            .setParameter("userId", userId)
            .getSingleResult();
  }

  public List<Loan> getPendingLoans() {
    return entityManager.createQuery("SELECT l FROM Loan l WHERE l.actualReturnDate IS NULL", Loan.class)
            .getResultList();
  }
}
