package com.library.dto.response;

import com.library.model.UserStatus;

import java.util.List;

public class UserDTO {
  private long id;
  private String name;
  private String email;
  private String cpf;
  private UserStatus status;
  private long profileId;
  private List<Long> loansIds;

  public UserDTO() {}

  public UserDTO(long id, String name, String email, String cpf, UserStatus status, long profileId, List<Long> loansIds) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.cpf = cpf;
    this.status = status;
    this.profileId = profileId;
    this.loansIds = loansIds;
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

  public long getProfileId() {
    return profileId;
  }

  public void setProfileId(long profileId) {
    this.profileId = profileId;
  }

  public List<Long> getLoansIds() {
    return loansIds;
  }

  public void setLoansIds(List<Long> loansIds) {
    this.loansIds = loansIds;
  }
}
