package com.library.dto.response;

import com.library.model.Role;

public class ProfileResponseDTO {
  private long id;
  private Role role;

  public ProfileResponseDTO(long id, Role role) {
    this.id = id;
    this.role = role;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
