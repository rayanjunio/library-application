package com.library.dto.request;

public class BookDTO {
  private String title;
  private String author;
  private int quantity;
  private int availableQuantity;

  public BookDTO() {}

  public BookDTO(String title, String author, int quantity, int availableQuantity) {
    this.title = title;
    this.author = author;
    this.quantity = quantity;
    this.availableQuantity = availableQuantity;
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
}
