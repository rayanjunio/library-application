package com.library.dao;

import com.library.model.Loan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class LoanDAO {
  @Inject
  EntityManager entityManager;

  @Transactional
  public void save(Loan loan) {
    entityManager.persist(loan);
  }

  @Transactional
  public Loan merge(Loan loan) {
    return entityManager.merge(loan);
  }

  @Transactional
  public void delete(long id) {
    Loan loan = this.findById(id);

    if(loan != null) entityManager.remove(loan);
  }

  public Loan findById(long id) {
    return entityManager.find(Loan.class, id);
  }

  public List<Loan> findAllFromUser(long userId) {
    return entityManager.createQuery("SELECT l FROM Loan l WHERE l.user.id = :userId ", Loan.class)
            .setParameter("userId", userId)
            .getResultList();
  }
}
