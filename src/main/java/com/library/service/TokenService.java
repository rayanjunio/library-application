package com.library.service;

import com.library.dto.internal.UserTokenInfo;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class TokenService {
  public String generateToken(UserTokenInfo userTokenInfo) {
    String role = userTokenInfo.getRole().toString();
    Set<String> groups = role.equalsIgnoreCase("ADMIN") ? Set.of("ADMIN") : Set.of("MEMBER");

    return Jwt.issuer("https://localhost:8080")
            .subject(userTokenInfo.getEmail())
            .groups(groups)
            .claim("userId", userTokenInfo.getId())
            .expiresIn(Duration.ofHours(1))
            .sign();
  }
}
