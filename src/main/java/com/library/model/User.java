package com.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotBlank(message = "Name cannot be blank")
  @Size(min = 2, max = 70, message = "Name must be between 2 and 70 characters")
  private String name;

  @NotBlank(message = "E-mail cannot be blank")
  @Email(message = "E-mail format is invalid")
  @Column(unique = true)
  private String email;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
  private String password;

  @NotBlank(message = "CPF cannot be blank")
  @CPF(message = "CPF is invalid")
  @Column(unique = true)
  private String cpf;

  @NotNull(message = "Status cannot be blank")
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @ManyToOne
  @JoinColumn(name = "profile_id")
  private Profile profile;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Loan> loans;

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
