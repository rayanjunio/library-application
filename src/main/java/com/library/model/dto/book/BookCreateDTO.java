package com.library.model.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookCreateDTO {
  @NotBlank(message = "ISBN cannot be blank")
  private String isbn;

  @NotBlank(message = "Title cannot be blank")
  private String title;

  @NotBlank(message = "Author name cannot be blank")
  private String author;

  @NotNull(message = "Quantity cannot be null")
  @Min(value = 1, message = "Quantity must be at least 1")
  private Integer quantity;

  public BookCreateDTO() {}

  public BookCreateDTO(String title, String author, int quantity) {
    this.title = title;
    this.author = author;
    this.quantity = quantity;
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
}
