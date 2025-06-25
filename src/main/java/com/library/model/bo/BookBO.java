package com.library.model.bo;

import com.library.model.dao.BookDAO;
import com.library.model.dto.book.BookCreateDTO;
import com.library.model.dto.book.BookUpdateDTO;
import com.library.model.dto.book.BookResponseDTO;
import com.library.model.mapper.BookMapper;
import com.library.model.entity.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class BookBO {

    @Inject
    BookDAO bookDAO;

    @Inject
    BookMapper bookMapper;

    @Transactional
    public BookResponseDTO create(BookCreateDTO bookCreateDTO) {
        Optional<Book> bookExists = bookDAO.findByIsbn(bookCreateDTO.getIsbn());

        if(bookExists.isPresent()) {
            throw new IllegalArgumentException("This book already exists.");
        }

        Book book = bookMapper.toEntity(bookCreateDTO);
        book.setAvailableQuantity(book.getQuantity());
        bookDAO.save(book);
        return bookMapper.toDTO(book);
    }

    public List<BookResponseDTO> findAll() {
        return bookDAO.findAllBooks().stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BookResponseDTO findByIsbn(String isbn) {
        Optional<Book> book = bookDAO.findByIsbn(isbn);
        if(book.isEmpty()) {
            throw new RuntimeException("This book does not exist");
        }
        return bookMapper.toDTO(book.get());
    }

    public List<BookResponseDTO> findAvailableBooks() {
        List<Book> books = bookDAO.findAvailableBooks(10);

        return books.stream()
                .map(book -> bookMapper.toDTO(book))
                .toList();
    }

    @Transactional
    public BookResponseDTO updateBook(String isbn, BookUpdateDTO bookUpdateDTO) {
        Optional<Book> bookExists = bookDAO.findByIsbn(isbn);
        if (bookExists.isEmpty()) {
            throw new IllegalArgumentException("This book doesn't exist.");
        }

        Book book = bookExists.get();

        if(bookUpdateDTO.getIsbn() != null) book.setIsbn(bookUpdateDTO.getIsbn());
        if(bookUpdateDTO.getTitle() != null) book.setTitle(bookUpdateDTO.getTitle());
        if(bookUpdateDTO.getAuthor() != null) book.setAuthor(bookUpdateDTO.getAuthor());
        if(bookUpdateDTO.getQuantity() != null) {
            int activeLoans = book.getQuantity() - book.getAvailableQuantity();
            book.setQuantity(bookUpdateDTO.getQuantity());
            book.setAvailableQuantity(activeLoans + book.getQuantity());
        }
        
        book = bookDAO.merge(book);
        return bookMapper.toDTO(book);
    }

    @Transactional
    public void delete(String isbn) {
        Optional<Book> book = bookDAO.findByIsbn(isbn);
        if(book.isEmpty()) {
            throw new IllegalArgumentException("This book doesn't exist.");
        }

        bookDAO.delete(book.get());
    }
} 