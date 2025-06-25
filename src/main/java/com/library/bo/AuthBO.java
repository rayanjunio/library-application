package com.library.bo;

import com.library.dao.UserDAO;
import com.library.dto.internal.UserTokenInfo;
import com.library.model.User;
import com.library.service.TokenService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class AuthBO {

  @Inject
  UserDAO userDAO;

  @Inject
  TokenService tokenService;

  public String authenticate(String email, String password) {
    Optional<User> user = userDAO.findByEmail(email.trim());

    if(user.isEmpty() || !BcryptUtil.matches(password, user.get().getPassword())) {
      throw new IllegalArgumentException("Invalid user credentials.");
    }

    UserTokenInfo userTokenInfo = new UserTokenInfo(user.get().getId(), email, user.get().getProfile().getRole());

    return tokenService.generateToken(userTokenInfo);
  }
}
