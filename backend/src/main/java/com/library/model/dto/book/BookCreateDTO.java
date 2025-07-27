package com.library.model.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookCreateDTO {
  @NotBlank(message = "O ISBN não pode estar em branco")
  private String isbn;

  @NotBlank(message = "O título não pode estar em branco")
  private String title;

  @NotBlank(message = "O nome do autor não pode estar em branco")
  private String author;

  @NotNull(message = "A quantidade não pode ser nula")
  @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
  private Integer quantity;

  public BookCreateDTO() {}

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
