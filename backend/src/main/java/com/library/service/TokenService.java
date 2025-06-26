package com.library.service;

import com.library.model.dto.auth.UserTokenInfoDTO;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class TokenService {
  public String generateToken(UserTokenInfoDTO userTokenInfoDTO) {
    String role = userTokenInfoDTO.getRole().toString();
    Set<String> groups = role.equalsIgnoreCase("ADMIN") ? Set.of("ADMIN") : Set.of("MEMBER");

    return Jwt.issuer("https://localhost:8080")
            .subject(userTokenInfoDTO.getEmail())
            .groups(groups)
            .claim("userId", userTokenInfoDTO.getId())
            .expiresIn(Duration.ofHours(1))
            .sign();
  }
}
