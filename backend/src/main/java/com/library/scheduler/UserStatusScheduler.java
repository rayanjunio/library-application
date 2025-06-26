package com.library.scheduler;

import com.library.model.dao.LoanDAO;
import com.library.model.dao.UserDAO;
import com.library.model.entity.Loan;
import com.library.model.entity.User;
import com.library.model.enums.UserStatus;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class UserStatusScheduler {
  @Inject
  LoanDAO loanDAO;

  @Inject
  UserDAO userDAO;

  @Scheduled(every = "1m")
  public void validatePendingLoans() {
    List<Loan> loans = loanDAO.getPendingLoans();

    // Mocked date used for testing (replace with LocalDate.now() in production)
    LocalDate today = LocalDate.of(2025, 6, 28);

    for(Loan loan : loans) {
      if(loan.getExpectedReturnDate().isBefore(today)) {
        User user = loan.getUser();
        user.setStatus(UserStatus.FINED);
        userDAO.merge(user);
      }
    }

    System.out.println("executing scheduling");
  }
}
