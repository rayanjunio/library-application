package com.library.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;

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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Book getBook() {
    return book;
  }

  public void setBook(Book book) {
    this.book = book;
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
