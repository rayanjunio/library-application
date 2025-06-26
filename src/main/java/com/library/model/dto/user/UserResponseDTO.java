package com.library.model.dto.user;

import com.library.model.dto.loan.LoanResponseDTO;
import com.library.model.enums.UserStatus;

import java.util.List;

public class UserResponseDTO {
  private long id;
  private String name;
  private String email;
  private String cpf;
  private UserStatus status;
  private String profile;
  private List<LoanResponseDTO> loans;

  public UserResponseDTO() {}

  public UserResponseDTO(long id, String name, String email, String cpf, UserStatus status, String profile, List<LoanResponseDTO> loans) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.cpf = cpf;
    this.status = status;
    this.profile = profile;
    this.loans = loans;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public List<LoanResponseDTO> getLoans() {
    return loans;
  }

  public void setLoans(List<LoanResponseDTO> loans) {
    this.loans = loans;
  }
}
