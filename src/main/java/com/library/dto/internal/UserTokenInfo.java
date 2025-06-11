package com.library.dto.internal;

import com.library.model.Role;

public class UserTokenInfo {
  private long id;
  private String email;
  private Role role;

  public UserTokenInfo(long id, String email, Role role) {
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
