package com.library.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoanRequestDTO {
  @NotNull(message = "User id cannot be null")
  private long userId;

  @NotNull(message = "Book id cannot be null")
  private long bookId;

  @NotNull(message = "Loan date cannot be null")
  @FutureOrPresent(message = "Expected return date cannot be in the past")
  private LocalDate loanDate;

  @NotNull(message = "Expected return date cannot be null")
  @FutureOrPresent(message = "Expected return date cannot be in the past")
  private LocalDate expectedReturnDate;

  public LoanRequestDTO() {}

  public LoanRequestDTO(long userId, long bookId, LocalDate loanDate, LocalDate expectedReturnDate) {
    this.userId = userId;
    this.bookId = bookId;
    this.loanDate = loanDate;
    this.expectedReturnDate = expectedReturnDate;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public long getBookId() {
    return bookId;
  }

  public void setBookId(long bookId) {
    this.bookId = bookId;
  }

  public LocalDate getLoanDate() {
    return loanDate;
  }

  public void setLoanDate(LocalDate loanDate) {
    this.loanDate = loanDate;
  }

  public LocalDate getExpectedReturnDate() {
    return expectedReturnDate;
  }

  public void setExpectedReturnDate(LocalDate expectedReturnDate) {
    this.expectedReturnDate = expectedReturnDate;
  }
}
