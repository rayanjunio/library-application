package com.library.model.dto.book;

import com.library.model.entity.Book;

public class BookResponseDTO {
  private Long id;
  private String isbn;
  private String title;
  private String author;
  private Integer quantity;
  private Integer availableQuantity;

  public BookResponseDTO() {}

  public BookResponseDTO(Book book) {
    this.id = book.getId();
    this.isbn = book.getIsbn();
    this.title = book.getTitle();
    this.author = book.getAuthor();
    this.quantity = book.getQuantity();
    this.availableQuantity = book.getAvailableQuantity();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Integer getAvailableQuantity() {
    return availableQuantity;
  }

  public void setAvailableQuantity(Integer availableQuantity) {
    this.availableQuantity = availableQuantity;
  }
}
