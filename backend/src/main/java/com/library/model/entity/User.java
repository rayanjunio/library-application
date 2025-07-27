package com.library.model.entity;

import com.library.model.dto.user.UserRequestDTO;
import com.library.model.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotBlank(message = "O nome não pode estar em branco")
  @Size(min = 2, max = 70, message = "O nome deve ter entre 2 e 70 caracteres")
  private String name;

  @NotBlank(message = "O e-mail não pode estar em branco")
  @Email(message = "O formato do e-mail é inválido")
  @Column(unique = true)
  private String email;

  @NotBlank(message = "A senha não pode estar em branco")
  private String password;

  @NotBlank(message = "O CPF não pode estar em branco")
  @CPF(message = "O CPF é inválido")
  @Column(unique = true)
  private String cpf;

  @NotNull(message = "O status não pode ser nulo")
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @ManyToOne
  @JoinColumn(name = "profile_id")
  private Profile profile;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Loan> loans = new ArrayList<>();

  public User() {}

  public User(UserRequestDTO userRequestDTO) {
    this.name = userRequestDTO.getName();
    this.email = userRequestDTO.getEmail();
    this.password = userRequestDTO.getPassword();
    this.cpf = userRequestDTO.getCpf();
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

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public List<Loan> getLoans() {
    return loans;
  }

  public void setLoans(List<Loan> loans) {
    this.loans = loans;
  }
}
