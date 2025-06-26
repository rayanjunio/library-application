package com.library.model.dto.loan;

import java.time.LocalDate;

public class LoanResponseDTO {
  private long id;
  private String userEmail;
  private String bookIsbn;
  private boolean isActive;
  private LocalDate loanDate;
  private LocalDate expectedReturnDate;
  private LocalDate actualReturnDate;

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

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
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

  public LocalDate getActualReturnDate() {
    return actualReturnDate;
  }

  public void setActualReturnDate(LocalDate actualReturnDate) {
    this.actualReturnDate = actualReturnDate;
  }

  public LoanResponseDTO() {}

  public LoanResponseDTO(long id, String userEmail, String bookIsbn, boolean isActive, LocalDate loanDate, LocalDate expectedReturnDate, LocalDate actualReturnDate) {
    this.id = id;
    this.userEmail = userEmail;
    this.bookIsbn = bookIsbn;
    this.isActive = isActive;
    this.loanDate = loanDate;
    this.expectedReturnDate = expectedReturnDate;
    this.actualReturnDate = actualReturnDate;
  }
}
