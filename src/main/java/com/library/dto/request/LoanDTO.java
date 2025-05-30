package com.library.dto.request;

import java.time.LocalDate;

public class LoanDTO {
  private long userId;
  private long bookId;
  private LocalDate loanDate;
  private LocalDate expectedReturnDate;

  public LoanDTO() {}

  public LoanDTO(long userId, long bookId, LocalDate loanDate, LocalDate expectedReturnDate) {
    this.userId = userId;
    this.bookId = bookId;
    this.loanDate = loanDate;
    this.expectedReturnDate = expectedReturnDate;
  }

}
