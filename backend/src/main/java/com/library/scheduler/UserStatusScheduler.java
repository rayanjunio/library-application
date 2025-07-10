package com.library.scheduler;

import com.library.model.bo.LoanBO;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserStatusScheduler {

  @Inject
  LoanBO loanBO;

  @Transactional
  @Scheduled(every = "1m")
  public void validatePendingLoans() {
    loanBO.fineUsersWithPendingLoans();
    System.out.println("executing scheduling");
  }
}
