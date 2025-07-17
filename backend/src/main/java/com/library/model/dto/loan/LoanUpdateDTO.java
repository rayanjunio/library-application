package com.library.model.dto.loan;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public class LoanUpdateDTO {
  private long id;

  @Email
  private String userEmail;

  private String bookIsbn;

  @FutureOrPresent(message = "Expected return date cannot be in the past")
  private LocalDate expectedReturnDate;

  public LoanUpdateDTO() {}

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getBookIsbn() {
    return bookIsbn;
  }

  public void setBookIsbn(String bookIsbn) {
    this.bookIsbn = bookIsbn;
  }

  public LocalDate getExpectedReturnDate() {
    return expectedReturnDate;
  }

  public void setExpectedReturnDate(LocalDate expectedReturnDate) {
    this.expectedReturnDate = expectedReturnDate;
  }
}
