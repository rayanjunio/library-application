package com.library.bo;

import com.library.dao.BookDAO;
import com.library.dto.request.BookRequestDTO;
import com.library.dto.response.BookResponseDTO;
import com.library.mapper.BookMapper;
import com.library.model.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BookBO {

    @Inject
    BookDAO bookDAO;

    @Inject
    BookMapper bookMapper;

    @Transactional
    public BookResponseDTO create(BookRequestDTO request) {
        Book book = bookMapper.toEntity(request);
        book.setAvailableQuantity(book.getQuantity());
        bookDAO.save(book);
        return bookMapper.toDTO(book);
    }

    public List<BookResponseDTO> findAll() {
        return bookDAO.findAll().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BookResponseDTO findByIsbn(String isbn) {
        Book book = bookDAO.findByIsbn(isbn);
        if(book == null) {
            throw new RuntimeException("This book does not exist");
        }
        return bookMapper.toDTO(book);
    }

    public List<BookResponseDTO> findAvailableBooks() {
        List<Book> books = bookDAO.findAvailableBooks(10);

        return books.stream()
                .map(book -> bookMapper.toDTO(book))
                .toList();
    }

    @Transactional
    public BookResponseDTO update(Long id, BookRequestDTO request) {
        Book book = bookDAO.findById(id);
        if (book == null) {
            throw new RuntimeException("Book not found");
        }
        
        // Atualiza os campos do livro
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setQuantity(request.getQuantity());
        
        book = bookDAO.merge(book);
        return bookMapper.toDTO(book);
    }

    @Transactional
    public void delete(Long id) {
        bookDAO.delete(id);
    }
} 