package com.library.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public class UserRequestDTO {
  @NotBlank(message = "Name cannot be blank")
  @Size(min = 2, max = 70, message = "Name must be between 2 and 70 characters")
  private String name;

  @NotBlank(message = "E-mail cannot be blank")
  @Email(message = "E-mail format is invalid")
  private String email;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  @NotBlank(message = "CPF cannot be blank")
  @CPF(message = "CPF is invalid")
  private String cpf;

  public UserRequestDTO() {}

  public UserRequestDTO(String name, String email, String password, String cpf) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.cpf = cpf;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }
}
