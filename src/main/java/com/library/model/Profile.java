package com.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

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
