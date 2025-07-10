package com.library.security;

import com.library.model.bo.LogBO;
import com.library.model.dto.log.LogDTO;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.json.JsonNumber;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;

@Provider
@Priority(1)
@Blocking
public class AuditFilter implements ContainerRequestFilter {

  @Inject
  LogBO logBO;

  @Inject
  JsonWebToken jwt;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String action = requestContext.getMethod() + " " + requestContext.getUriInfo().getPath();
    long userId = -1L;

    if (jwt != null && jwt.containsClaim("userId")) {
      JsonNumber jsonNumber = jwt.getClaim("userId");
      userId = jsonNumber.longValue();
    }

    logBO.create(new LogDTO(action, userId, LocalDateTime.now()));
  }
}
