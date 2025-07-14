package com.library.service;

import com.library.exception.type.BusinessException;
import com.library.model.dao.UserDAO;
import com.library.model.dto.auth.UserTokenInfoDTO;
import com.library.model.entity.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.NewCookie;

import java.util.Optional;

@ApplicationScoped
public class AuthService {

  @Inject
  UserDAO userDAO;

  @Inject
  TokenService tokenService;

  public NewCookie authenticate(String email, String password) {
    Optional<User> user = userDAO.findByEmail(email.trim());

    if(user.isEmpty() || !BcryptUtil.matches(password, user.get().getPassword())) {
      throw new BusinessException("Invalid user credentials", 401);
    }

    UserTokenInfoDTO userTokenInfoDTO = new UserTokenInfoDTO(user.get().getId(), email, user.get().getProfile().getRole());
    String token = tokenService.generateToken(userTokenInfoDTO);

    return new NewCookie.Builder("jwt")
            .value(token)
            .path("/")
            .maxAge(3600)
            .httpOnly(true)
            .secure(false)
            .sameSite(NewCookie.SameSite.LAX)
            .build();
  }
}