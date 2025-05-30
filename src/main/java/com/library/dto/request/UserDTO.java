package com.library.dto.request;

import com.library.model.UserStatus;

public class UserDTO {
  private String name;
  private String email;
  private String password;
  private String cpf;
  private UserStatus status;
  private long profileId;

  public UserDTO() {}

  public UserDTO(String name, String email, String password, String cpf, UserStatus status, long profileId) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.cpf = cpf;
    this.status = status;
    this.profileId = profileId;
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

  public long getProfileId() {
    return profileId;
  }

  public void setProfileId(long profileId) {
    this.profileId = profileId;
  }
}
