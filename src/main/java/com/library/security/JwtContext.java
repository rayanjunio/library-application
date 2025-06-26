package com.library.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonNumber;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class JwtContext {

  @Inject
  JsonWebToken jwt;

  public long getUserId() {
    JsonNumber jsonNumber = jwt.getClaim("userId");
    return jsonNumber.longValue();
  }

  public String getUserRole() {
    System.out.println(jwt.getGroups().toString());
    return jwt.getGroups().toString();
  }
}
