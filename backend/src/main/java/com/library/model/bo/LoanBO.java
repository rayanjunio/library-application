package com.library.model.bo;

import com.library.exception.type.BusinessException;
import com.library.model.dao.BookDAO;
import com.library.model.dao.LoanDAO;
import com.library.model.dao.UserDAO;
import com.library.model.dto.PagedResponseDTO;
import com.library.model.dto.loan.LoanRequestDTO;
import com.library.model.dto.loan.LoanResponseDTO;
import com.library.model.dto.loan.LoanUpdateDTO;
import com.library.model.entity.Book;
import com.library.model.entity.Loan;
import com.library.model.entity.User;
import com.library.model.enums.UserStatus;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class LoanBO {

  @Inject
  UserDAO userDAO;

  @Inject
  BookDAO bookDAO;

  @Inject
  LoanDAO loanDAO;

  @Transactional
  public LoanResponseDTO createLoan(LoanRequestDTO loanRequestDTO) {
    if(loanRequestDTO.getExpectedReturnDate().isBefore(LocalDate.now())) {
      throw new BusinessException("The expected return date must be after today", 400);
    }

    Period period = Period.between(LocalDate.now(), loanRequestDTO.getExpectedReturnDate());
    if(period.getDays() > 15) {
      throw new BusinessException("The loan period cannot exceed 15 days", 400);
    }

    Optional<User> userExists = userDAO.findByEmail(loanRequestDTO.getUserEmail());
    if(userExists.isEmpty()) {
      throw new BusinessException("This user does not exist", 400);
    }

    User user = userExists.get();

    if(user.getStatus() == UserStatus.FINED) {
      throw new BusinessException("This user cannot take out more loans until he settles his pending loans", 400);
    }

    if(user.getProfile().getRole().name().equals("ADMIN")) {
      throw new BusinessException("Admins cannot create loans for themselves. Please, use your personal account", 400);
    }

    if(loanDAO.countUserPendingLoans(user.getId()) != 0) {
      throw new BusinessException("This user cannot take out more loans because he already has one active loan", 400);
    }

    Optional<Book> bookExists = bookDAO.findByIsbn(loanRequestDTO.getBookIsbn());
    if(bookExists.isEmpty()) {
      throw new BusinessException("This book does not exist", 400);
    }

    Book book = bookExists.get();

    if(book.getAvailableQuantity() <= 0) {
      throw new BusinessException("This book is unavailable at the moment", 400);
    }

    book.setAvailableQuantity(book.getAvailableQuantity() - 1);
    bookDAO.merge(book);

    Loan loan = new Loan(loanRequestDTO.getExpectedReturnDate());
    loan.setUser(user);
    loan.setBook(book);
    loan.setLoanDate(LocalDate.now());

    loanDAO.save(loan);
    return new LoanResponseDTO(loan);
  }

  @Transactional
  public LoanResponseDTO updateLoan(LoanUpdateDTO loanUpdateDTO) {
    Loan loan = loanDAO.findById(loanUpdateDTO.getId());

    if(loan == null) {
      throw new BusinessException("Loan not found", 404);
    }

    if(loanUpdateDTO.getUserEmail() != null) {
      User user = userDAO.findByEmail(loanUpdateDTO.getUserEmail()).orElse(null);

      if(user == null) {
        throw new BusinessException("User not found", 404);
      }

      if(user.getStatus() == UserStatus.FINED) {
        throw new BusinessException("This user cannot take out more loans until he settles his pending loans", 400);
      }

      if(user.getProfile().getRole().name().equals("ADMIN")) {
        throw new BusinessException("Admins cannot create loans for themselves. Please, use your personal account", 400);
      }

      if(loanDAO.countUserActiveLoansExcluding(loan.getId(), user.getId()) != 0) {
        throw new BusinessException("This user cannot take out more loans because he already has one active loan", 400);
      }

      loan.setUser(user);
    }

    if(loanUpdateDTO.getBookIsbn() != null) {
      Book book = bookDAO.findByIsbn(loanUpdateDTO.getBookIsbn()).orElse(null);

      if(book == null) {
        throw new BusinessException("Book not found", 404);
      }

      if(book.getAvailableQuantity() <= 0) {
        throw new BusinessException("This book is unavailable at the moment", 400);
      }

      Book oldBook = loan.getBook();
      oldBook.setAvailableQuantity(oldBook.getAvailableQuantity() + 1);
      bookDAO.merge(oldBook);

      book.setAvailableQuantity(loan.getBook().getAvailableQuantity() - 1);
      bookDAO.merge(book);
      loan.setBook(book);
    }

    if(loanUpdateDTO.getExpectedReturnDate() != null) {
      if (loanUpdateDTO.getExpectedReturnDate().isBefore(LocalDate.now())) {
        throw new BusinessException("The expected return date must be after today", 400);
      }

      Period period = Period.between(loan.getLoanDate(), loanUpdateDTO.getExpectedReturnDate());
      if(period.getDays() > 15) {
        throw new BusinessException("The loan period cannot exceed 15 days", 400);
      }
      loan.setExpectedReturnDate(loanUpdateDTO.getExpectedReturnDate());
    }

    loanDAO.merge(loan);
    return new LoanResponseDTO(loan);
  }

  @Transactional
  public LoanResponseDTO finishLoan(int loanId) {
    Loan loan = loanDAO.findById(loanId);
    if(loan == null) {
      throw new BusinessException("This loan does not exist", 400);
    }

    loan.setActualReturnDate(LocalDate.now());
    loan.setActive(false);

    Book book = bookDAO.findById(loan.getBook().getId());
    book.setAvailableQuantity(book.getAvailableQuantity() + 1);

    bookDAO.merge(book);
    loanDAO.merge(loan);

    return new LoanResponseDTO(loan);
  }

  @Transactional
  public void removeUserFine(String email) {
    Optional<User> userExists = userDAO.findByEmail(email);
    if(userExists.isEmpty()) {
      throw new BusinessException("User not found", 404);
    }

    User user = userExists.get();

    if(user.getStatus() == UserStatus.FINED) {
      user.setStatus(UserStatus.ACTIVE);
      userDAO.merge(user);
    }
  }

  @Transactional
  public void deleteLoan(long loanId) {
    Loan loan = loanDAO.findById(loanId);

    if(loan == null) {
      throw new BusinessException("Loan not found", 404);
    }

    loanDAO.delete(loan);
  }

  @Transactional
  public void fineUsersWithPendingLoans() {
    loanDAO.fineUsersWithPendingLoans();
  }

  public long countUserActiveLoans(long userId) {
    return loanDAO.countUserActiveLoans(userId);
  }

  public long countAllActiveLoans() {
    return loanDAO.countAllActiveLoans();
  }

  public PagedResponseDTO<LoanResponseDTO> getUserActiveLoans(long userId, int page, int size) {
    List<Loan> allLoans = loanDAO.getUserActiveLoans(userId, page, size);
    long total = loanDAO.countUserActiveLoans(userId);

    List<LoanResponseDTO> allLoansResponse =
            allLoans.stream()
            .map(LoanResponseDTO::new)
            .toList();
    return new PagedResponseDTO<>(total, page, size, allLoansResponse);
  }

  public PagedResponseDTO<LoanResponseDTO> getActiveLoans(int page, int size) {
    List<Loan> loans = loanDAO.getActiveLoans(page, size);
    long total = loanDAO.countAllActiveLoans();
    List<LoanResponseDTO> allActiveLoans = loans.stream()
            .map(LoanResponseDTO::new)
            .toList();
    return new PagedResponseDTO<>(total, page, size, allActiveLoans);
  }
}
