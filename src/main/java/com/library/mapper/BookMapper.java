package com.library.mapper;

import com.library.dto.request.book.BookCreateDTO;
import com.library.dto.response.BookResponseDTO;
import com.library.model.Book;
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
