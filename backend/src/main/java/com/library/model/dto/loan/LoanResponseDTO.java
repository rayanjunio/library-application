package com.library.model.dto.loan;

import com.library.model.entity.Loan;

import java.time.LocalDate;

public class LoanResponseDTO {
  private long id;
  private String userEmail;
  private String bookIsbn;
  private String bookTitle;
  private String bookAuthor;
  private boolean isActive;
  private LocalDate loanDate;
  private LocalDate expectedReturnDate;
  private LocalDate actualReturnDate;

  public LoanResponseDTO() {}

  public LoanResponseDTO(Loan loan) {
    this.id = loan.getId();
    this.userEmail = loan.getUser().getEmail();
    this.bookIsbn = loan.getBook().getIsbn();
    this.bookTitle = loan.getBook().getTitle();
    this.bookAuthor = loan.getBook().getAuthor();
    this.isActive = loan.isActive();
    this.loanDate = loan.getLoanDate();
    this.expectedReturnDate = loan.getExpectedReturnDate();
    this.actualReturnDate = loan.getActualReturnDate();
  }

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

  public String getBookTitle() {
    return bookTitle;
  }

  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }

  public String getBookAuthor() {
    return bookAuthor;
  }

  public void setBookAuthor(String bookAuthor) {
    this.bookAuthor = bookAuthor;
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
}
