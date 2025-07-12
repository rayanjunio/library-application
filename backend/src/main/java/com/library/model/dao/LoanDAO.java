package com.library.model.dao;

import com.library.model.entity.Loan;
import com.library.model.enums.UserStatus;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;

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

  public long countUserPendingLoans(long userId) {
    return entityManager.createQuery("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.actualReturnDate IS NULL", Long.class)
            .setParameter("userId", userId)
            .getSingleResult();
  }

  public void fineUsersWithPendingLoans() {
    entityManager.createQuery("UPDATE User u SET status = :finedStatus " +
                    "WHERE u IN (SELECT l.user FROM Loan l WHERE l.expectedReturnDate < :today AND l.isActive = true)")
            .setParameter("finedStatus", UserStatus.FINED)
            .setParameter("today", LocalDate.of(2025, 7, 12)) // this date must be set to LocalDate.now()
            .executeUpdate();
  }
}
