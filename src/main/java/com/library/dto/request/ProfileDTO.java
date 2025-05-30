package com.library.dto.request;

import com.library.model.Role;

public class ProfileDTO {
  private Role role;

  public ProfileDTO() {}

  public ProfileDTO(Role role) {
    this.role = role;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
