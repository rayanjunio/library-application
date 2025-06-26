package com.library.model.mapper;

import com.library.model.dto.book.BookCreateDTO;
import com.library.model.dto.book.BookResponseDTO;
import com.library.model.entity.Book;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookMapper {

  public Book toEntity(BookCreateDTO dto) {
    Book book = new Book();
    book.setIsbn(dto.getIsbn());
    book.setTitle(dto.getTitle());
    book.setAuthor(dto.getAuthor());
    book.setQuantity(dto.getQuantity());
    book.setAvailableQuantity(dto.getQuantity());
    return book;
  }

  public BookResponseDTO toDTO(Book book) {
    return new BookResponseDTO(
            book.getId(),
            book.getIsbn(),
            book.getTitle(),
            book.getAuthor(),
            book.getQuantity(),
            book.getAvailableQuantity()
    );
  }
}
