package com.library.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public class UserRequestDTO {
  @NotBlank(message = "O nome não pode estar em branco")
  @Size(min = 2, max = 70, message = "O nome deve ter entre 2 e 70 caracteres")
  private String name;

  @NotBlank(message = "O e-mail não pode estar em branco")
  @Email(message = "O formato do e-mail é inválido")
  private String email;

  @NotBlank(message = "A senha não pode estar em branco")
  @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
  private String password;

  @NotBlank(message = "O CPF não pode estar em branco")
  @CPF(message = "O CPF informado é inválido")
  private String cpf;

  public UserRequestDTO() {}

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
