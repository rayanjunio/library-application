package com.library.security;

import com.library.model.dto.auth.UserTokenInfoDTO;
import com.library.model.enums.Role;
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

  public UserTokenInfoDTO getUserInfo() {
    long userId = getUserId();
    String email = jwt.getClaim("email");
    String roleString = jwt.getGroups().iterator().next();
    Role role = Role.valueOf(roleString);

    return new UserTokenInfoDTO(userId, email, role);
  }
}
