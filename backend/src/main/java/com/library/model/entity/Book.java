package com.library.model.entity;

import com.library.model.dto.book.BookCreateDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "books")
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotBlank(message = "O ISBN não pode estar em branco")
  @Column(unique = true, nullable = false)
  private String isbn;

  @NotBlank(message = "O título não pode estar em branco")
  private String title;

  @NotBlank(message = "O nome do autor não pode estar em branco")
  private String author;

  @NotNull(message = "A quantidade não pode ser nula")
  @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
  private int quantity;

  private int availableQuantity;

  @OneToMany(mappedBy = "book")
  private List<Loan> loans;

  public Book() {}

  public Book(BookCreateDTO bookCreateDTO) {
    this.isbn = bookCreateDTO.getIsbn();
    this.title = bookCreateDTO.getTitle();
    this.author = bookCreateDTO.getAuthor();
    this.quantity = bookCreateDTO.getQuantity();
    this.availableQuantity = bookCreateDTO.getQuantity();
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

  public List<Loan> getLoans() {
    return loans;
  }

  public void setLoans(List<Loan> loans) {
    this.loans = loans;
  }
}
