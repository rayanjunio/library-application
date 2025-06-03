package com.library.dto.response;

import java.util.List;

public class BookDTO {
  private long id;
  private String title;
  private String author;
  private int quantity;
  private int availableQuantity;
  private List<Long> loansIds;

  public BookDTO() {}

  public BookDTO(long id, String title, String author, int quantity, int availableQuantity, List<Long> loansIds) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.quantity = quantity;
    this.availableQuantity = availableQuantity;
    this.loansIds = loansIds;
  }

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

  public List<Long> getLoansIds() {
    return loansIds;
  }

  public void setLoansIds(List<Long> loansIds) {
    this.loansIds = loansIds;
  }
}
