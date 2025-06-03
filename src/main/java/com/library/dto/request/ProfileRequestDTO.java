package com.library.dto.request;

import com.library.model.Role;
import jakarta.validation.constraints.NotNull;

public class ProfileRequestDTO {
  @NotNull(message = "Role cannot be null")
  private Role role;

  public ProfileRequestDTO() {}

  public ProfileRequestDTO(Role role) {
    this.role = role;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
