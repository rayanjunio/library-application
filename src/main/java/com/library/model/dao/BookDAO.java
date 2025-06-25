package com.library.model.dao;

import com.library.model.entity.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookDAO implements PanacheRepository<Book> {
  @Inject
  EntityManager entityManager;

  @Transactional
  public void save(Book book) {
    entityManager.persist(book);
  }

  @Transactional
  public Book merge(Book book) {
    return entityManager.merge(book);
  }

  @Transactional
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
}
