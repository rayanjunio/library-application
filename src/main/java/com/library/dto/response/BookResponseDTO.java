package com.library.dto.response;

public class BookResponseDTO {
  private long id;
  private String isbn;
  private String title;
  private String author;
  private int quantity;
  private int availableQuantity;

  public BookResponseDTO() {}

  public BookResponseDTO(long id, String isbn, String title, String author, int quantity, int availableQuantity) {
    this.id = id;
    this.isbn = isbn;
    this.title = title;
    this.author = author;
    this.quantity = quantity;
    this.availableQuantity = availableQuantity;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
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
