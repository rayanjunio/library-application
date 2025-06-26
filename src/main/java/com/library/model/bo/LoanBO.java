package com.library.model.bo;

import com.library.model.dao.BookDAO;
import com.library.model.dao.LoanDAO;
import com.library.model.dao.UserDAO;
import com.library.model.dto.loan.LoanRequestDTO;
import com.library.model.dto.loan.LoanResponseDTO;
import com.library.model.mapper.LoanMapper;
import com.library.model.entity.Book;
import com.library.model.entity.Loan;
import com.library.model.entity.User;
import com.library.model.enums.UserStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@ApplicationScoped
public class LoanBO {

  @Inject
  UserDAO userDAO;

  @Inject
  BookDAO bookDAO;

  @Inject
  LoanDAO loanDAO;

  @Inject
  LoanMapper loanMapper;

  public LoanResponseDTO createLoan(LoanRequestDTO loanRequestDTO) {
    if(loanRequestDTO.getExpectedReturnDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("The expected return date must be after today");
    }

    Period period = Period.between(LocalDate.now(), loanRequestDTO.getExpectedReturnDate());
    if(period.getDays() > 15) {
      throw new IllegalArgumentException("The loan period cannot exceed 15 days.");
    }

    Optional<User> userExists = userDAO.findByEmail(loanRequestDTO.getUserEmail());
    if(userExists.isEmpty()) {
      throw new IllegalArgumentException("This user doesn't exist");
    }

    User user = userExists.get();

    if(user.getStatus() == UserStatus.FINED) {
      throw new IllegalArgumentException("This user cannot take out more loans until he settles his pending loans");
    }

    if(user.getProfile().getRole().name().equals("ADMIN")) {
      throw new IllegalArgumentException("Admins cannot create loans for themselves. Please use your personal account");
    }

    if(loanDAO.countUserPendingLoans(user.getId()) != 0) {
      throw new IllegalArgumentException("This user cannot take out more loans because he already has one active loan");
    }

    Optional<Book> bookExists = bookDAO.findByIsbn(loanRequestDTO.getBookIsbn());
    if(bookExists.isEmpty()) {
      throw new IllegalArgumentException("This book doesn't exist");
    }

    Book book = bookExists.get();

    if(book.getAvailableQuantity() <= 0) {
      throw new IllegalArgumentException("This book is unavailable at the moment.");
    }

    book.setAvailableQuantity(book.getAvailableQuantity() - 1);
    bookDAO.merge(book);

    Loan loan = loanMapper.toEntity(loanRequestDTO);
    loan.setUser(user);
    loan.setBook(book);
    loan.setLoanDate(LocalDate.now());
    loanDAO.save(loan);
    return loanMapper.toDto(loan);
  }

  public LoanResponseDTO finishLoan(int loanId) {
    Loan loan = loanDAO.findById(loanId);
    if(loan == null) {
      throw new IllegalArgumentException("This loan doesn't exist");
    }

    loan.setActualReturnDate(LocalDate.now());
    loan.setActive(false);

    Book book = bookDAO.findById(loan.getBook().getId());
    book.setAvailableQuantity(book.getAvailableQuantity() + 1);

    bookDAO.merge(book);
    loanDAO.merge(loan);

    return loanMapper.toDto(loan);
  }

  public void removeUserFine(String email) {
    Optional<User> userExists = userDAO.findByEmail(email);
    if(userExists.isEmpty()) {
      throw new IllegalArgumentException("User not found");
    }

    User user = userExists.get();

    if(user.getStatus() == UserStatus.FINED) {
      user.setStatus(UserStatus.ACTIVE);
      userDAO.merge(user);
    }
  }
}
