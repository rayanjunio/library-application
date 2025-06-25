package com.library.model.dto.loan;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoanRequestDTO {
  @NotBlank(message = "User email cannot be blank")
  private String userEmail;

  @NotBlank(message = "Book isbn cannot be blank")
  private String bookIsbn;

  @NotNull(message = "Expected return date cannot be null")
  @FutureOrPresent(message = "Expected return date cannot be in the past")
  private LocalDate expectedReturnDate;

  public LoanRequestDTO() {}

  public LoanRequestDTO(String userEmail, String bookIsbn, LocalDate expectedReturnDate) {
    this.userEmail = userEmail;
    this.bookIsbn = bookIsbn;
    this.expectedReturnDate = expectedReturnDate;
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
