package com.library.bo;

import com.library.dao.BookDAO;
import com.library.dao.LoanDAO;
import com.library.dao.UserDAO;
import com.library.dto.request.loan.LoanRequestDTO;
import com.library.dto.response.LoanResponseDTO;
import com.library.mapper.LoanMapper;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;
import com.library.model.UserStatus;
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
}
