package com.library.service;

import com.library.exception.type.BusinessException;
import com.library.model.bo.LogBO;
import com.library.model.dao.UserDAO;
import com.library.model.dto.auth.UserTokenInfoDTO;
import com.library.model.dto.log.LogDTO;
import com.library.model.entity.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

  @Inject
  UserDAO userDAO;

  @Inject
  LogBO logBO;

  @Inject
  TokenService tokenService;

  public String authenticate(String email, String password) {
    Optional<User> user = userDAO.findByEmail(email.trim());

    if(user.isEmpty() || !BcryptUtil.matches(password, user.get().getPassword())) {
      logBO.create(new LogDTO("POST /auth/login", -1L, 401, LocalDateTime.now()));
      throw new BusinessException("Invalid user credentials", 401);
    }

    UserTokenInfoDTO userTokenInfoDTO = new UserTokenInfoDTO(user.get().getId(), email, user.get().getProfile().getRole());

    logBO.create(new LogDTO("POST /auth/login", user.get().getId(), 200, LocalDateTime.now()));

    return tokenService.generateToken(userTokenInfoDTO);
  }
}