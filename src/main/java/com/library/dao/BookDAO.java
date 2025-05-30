package com.library.dao;

import com.library.model.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BookDAO {
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
  public void delete(long id) {
    Book book = this.findById(id);

    if(book != null) entityManager.remove(book);
  }

  public Book findById(long id) {
    return entityManager.find(Book.class, id);
  }

  public List<Book> findAll() {
    return entityManager.createQuery("SELECT b FROM Book b", Book.class).getResultList();
  }
}
