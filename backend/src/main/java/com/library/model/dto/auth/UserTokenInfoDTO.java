package com.library.model.dto.auth;

import com.library.model.enums.Role;

public class UserTokenInfoDTO {
  private long id;
  private String email;
  private Role role;

  public UserTokenInfoDTO(long id, String email, Role role) {
    this.id = id;
    this.email = email;
    this.role = role;
  }

  public long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
