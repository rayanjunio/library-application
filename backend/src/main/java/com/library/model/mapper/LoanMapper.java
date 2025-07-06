package com.library.model.mapper;

import com.library.model.dto.loan.LoanRequestDTO;
import com.library.model.dto.loan.LoanResponseDTO;
import com.library.model.entity.Loan;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoanMapper {

  public Loan toEntity(LoanRequestDTO dto) {
    Loan loan = new Loan();
    loan.setActive(true);
    loan.setExpectedReturnDate(dto.getExpectedReturnDate());
    loan.setActualReturnDate(null);
    return loan;
  }
  
  public LoanResponseDTO toDto(Loan loan) {
    LoanResponseDTO loanResponseDTO = new LoanResponseDTO();
    loanResponseDTO.setId(loan.getId());
    loanResponseDTO.setUserEmail(loan.getUser().getEmail());
    loanResponseDTO.setBookIsbn(loan.getBook().getIsbn());
    loanResponseDTO.setBookTitle(loan.getBook().getTitle());
    loanResponseDTO.setBookAuthor(loan.getBook().getAuthor());
    loanResponseDTO.setActive(loan.isActive());
    loanResponseDTO.setLoanDate(loan.getLoanDate());
    loanResponseDTO.setExpectedReturnDate(loan.getExpectedReturnDate());
    loanResponseDTO.setActualReturnDate(loan.getActualReturnDate());
    return loanResponseDTO;
  }
}
