package com.library.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  private String email;

  private String password;

  private String cpf;

  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @ManyToOne
  @JoinColumn(name = "profile_id")
  private Profile profile;

  @OneToMany(mappedBy = "user")
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
