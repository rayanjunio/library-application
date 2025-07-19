package com.library.model.bo;

import com.library.exception.type.BusinessException;
import com.library.model.dao.BookDAO;
import com.library.model.dto.PagedResponseDTO;
import com.library.model.dto.book.BookCreateDTO;
import com.library.model.dto.book.BookUpdateDTO;
import com.library.model.dto.book.BookResponseDTO;
import com.library.model.entity.Book;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class BookBO {

    @Inject
    BookDAO bookDAO;

    @Transactional
    public BookResponseDTO create(BookCreateDTO bookCreateDTO) {
        Optional<Book> bookExists = bookDAO.findByIsbn(bookCreateDTO.getIsbn());

        if(bookExists.isPresent()) {
            throw new BusinessException("This book already exists", 400);
        }

        Book book = new Book(bookCreateDTO);
        book.setAvailableQuantity(book.getQuantity());
        bookDAO.save(book);

        return new BookResponseDTO(book);
    }

    public PagedResponseDTO<BookResponseDTO> findAll(int page, int size) {
        List<BookResponseDTO> books = bookDAO.findAllBooks(page, size)
                .stream()
                .map(BookResponseDTO::new)
                .toList();
        long total = bookDAO.countAllBooks();

        return new PagedResponseDTO<>(total, page, size, books);
    }

    public BookResponseDTO findByIsbn(String isbn) {
        Optional<Book> book = bookDAO.findByIsbn(isbn);
        if(book.isEmpty()) {
            throw new BusinessException("This book does not exist", 400);
        }
        return new BookResponseDTO(book.get());
    }

    public PagedResponseDTO<BookResponseDTO> findAvailableBooks(int page, int size) {
        List<Book> books = bookDAO.findAvailableBooks(page, size);
        long total = bookDAO.countAvailableBooks();

        List<BookResponseDTO> availableBooks =
                books.stream()
                .map(BookResponseDTO::new)
                .toList();

        return new PagedResponseDTO<>(total, page, size, availableBooks);
    }

    @Transactional
    public BookResponseDTO updateBook(String isbn, BookUpdateDTO bookUpdateDTO) {
        Optional<Book> bookExists = bookDAO.findByIsbn(isbn);
        if (bookExists.isEmpty()) {
            throw new BusinessException("This book does not exist", 400);
        }

        Book book = bookExists.get();

        if(bookUpdateDTO.getIsbn() != null) book.setIsbn(bookUpdateDTO.getIsbn());
        if(bookUpdateDTO.getTitle() != null) book.setTitle(bookUpdateDTO.getTitle());
        if(bookUpdateDTO.getAuthor() != null) book.setAuthor(bookUpdateDTO.getAuthor());
        if(bookUpdateDTO.getQuantity() != null) {
            int activeLoans = book.getQuantity() - book.getAvailableQuantity();
            book.setQuantity(bookUpdateDTO.getQuantity());
            book.setAvailableQuantity(book.getQuantity() - activeLoans);
        }
        
        book = bookDAO.merge(book);
        return new BookResponseDTO(book);
    }

    @Transactional
    public void delete(String isbn) {
        Optional<Book> book = bookDAO.findByIsbn(isbn);
        if(book.isEmpty()) {
            throw new BusinessException("This book does not exist", 400);
        }

        bookDAO.delete(book.get());
    }

    public long countAvailableBooks() {
        return bookDAO.countAvailableBooks();
    }

    public long countAllBooks() {
        return bookDAO.countAllBooks();
    }
} 