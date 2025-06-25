package com.library.model.dto.profile;

import com.library.model.enums.Role;

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
