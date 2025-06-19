package com.library.mapper;

import com.library.dto.request.loan.LoanRequestDTO;
import com.library.dto.response.LoanResponseDTO;
import com.library.model.Loan;
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
    loanResponseDTO.setActive(loan.isActive());
    loanResponseDTO.setLoanDate(loan.getLoanDate());
    loanResponseDTO.setExpectedReturnDate(loan.getExpectedReturnDate());
    loanResponseDTO.setActualReturnDate(loan.getActualReturnDate());
    return loanResponseDTO;
  }
}
