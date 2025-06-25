package com.library.service;

import com.library.model.dao.UserDAO;
import com.library.model.dto.auth.UserTokenInfoDTO;
import com.library.model.entity.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class AuthService {

  @Inject
  UserDAO userDAO;

  @Inject
  TokenService tokenService;

  public String authenticate(String email, String password) {
    Optional<User> user = userDAO.findByEmail(email.trim());

    if(user.isEmpty() || !BcryptUtil.matches(password, user.get().getPassword())) {
      throw new IllegalArgumentException("Invalid user credentials.");
    }

    UserTokenInfoDTO userTokenInfoDTO = new UserTokenInfoDTO(user.get().getId(), email, user.get().getProfile().getRole());

    return tokenService.generateToken(userTokenInfoDTO);
  }
}
