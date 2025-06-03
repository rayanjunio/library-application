package com.library.dto.response;

import java.time.LocalDate;

public class LoanDTO {
  private long id;
  private long userId;
  private long bookId;
  private boolean isActive;
  private LocalDate loanDate;
  private LocalDate expectedReturnDate;
  private LocalDate actualReturnDate;

  public LoanDTO() {}

  public LoanDTO(long id, long userId, long bookId, boolean isActive, LocalDate loanDate, LocalDate expectedReturnDate, LocalDate actualReturnDate) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.isActive = isActive;
    this.loanDate = loanDate;
    this.expectedReturnDate = expectedReturnDate;
    this.actualReturnDate = actualReturnDate;
  }
}
