package com.library.model.dao;

import com.library.model.entity.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class BookDAO implements PanacheRepository<Book> {
  @Inject
  EntityManager entityManager;

  public void save(Book book) {
    entityManager.persist(book);
  }

  public Book merge(Book book) {
    return entityManager.merge(book);
  }

  public void delete(Book book) {
    entityManager.remove(book);
  }

  public Optional<Book> findByIsbn(String isbn) {
    return find("isbn", isbn).firstResultOptional();
  }

  public List<Book> findAvailableBooks(int limit) {
    return entityManager.createQuery("SELECT b FROM Book b WHERE b.availableQuantity > 0", Book.class)
            .setMaxResults(limit)
            .getResultList();
  }

  public List<Book> findAllBooks() {
    return findAll().stream().toList();
  }

  public long countAvailableBooks() {
    return entityManager.createQuery("SELECT COUNT(b) FROM Book b WHERE b.availableQuantity > 0", Long.class)
            .getSingleResult();
  }

  public long countAllBooks() {
    return entityManager.createQuery("SELECT COUNT(b) FROM Book b", Long.class)
            .getSingleResult();
  }
}
