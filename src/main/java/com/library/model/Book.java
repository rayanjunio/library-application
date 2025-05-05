package com.library.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "books")
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String title;

  private String author;

  private int quantity;

  private int availableQuantity;

  @OneToMany(mappedBy = "book")
  private List<Loan> loans;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public int getAvailableQuantity() {
    return availableQuantity;
  }

  public void setAvailableQuantity(int availableQuantity) {
    this.availableQuantity = availableQuantity;
  }

  public List<Loan> getLoans() {
    return loans;
  }

  public void setLoans(List<Loan> loans) {
    this.loans = loans;
  }
}
