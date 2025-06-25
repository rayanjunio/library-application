package com.library.model.entity;

import com.library.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "profiles")
public class Profile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull(message = "Role cannot be null")
  @Enumerated(EnumType.STRING)
  private Role role;

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
