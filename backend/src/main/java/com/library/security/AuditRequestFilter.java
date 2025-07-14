package com.library.security;

import com.library.model.dto.log.RequestLogContextDTO;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.json.JsonNumber;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;

@Provider
@Priority(10)
public class AuditRequestFilter implements ContainerRequestFilter {

  @Inject
  RequestLogContextDTO context;

  @Inject
  JsonWebToken jwt;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    context.setAction(requestContext.getMethod() + " " + requestContext.getUriInfo().getPath());
    context.setTimestamp(LocalDateTime.now());
    long userId = -1L;

    if (jwt != null && jwt.containsClaim("userId")) {
      JsonNumber jsonNumber = jwt.getClaim("userId");
      userId = jsonNumber.longValue();
    }
    context.setUserId(userId);
  }
}
