package com.library.model.dto.loan;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoanRequestDTO {
  @NotBlank(message = "O e-mail do usuário não pode estar em branco")
  private String userEmail;

  @NotBlank(message = "O ISBN do livro não pode estar em branco")
  private String bookIsbn;

  @NotNull(message = "A data de devolução esperada não pode ser nula")
  @FutureOrPresent(message = "A data de devolução esperada não pode estar no passado")
  private LocalDate expectedReturnDate;

  public LoanRequestDTO() {}

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
