package com.library.security;

import com.library.model.bo.LogBO;
import com.library.model.dto.log.LogDTO;
import jakarta.inject.Inject;
import jakarta.json.JsonNumber;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;

@Provider
public class AuditFilter implements ContainerRequestFilter, ContainerResponseFilter {

  @Inject
  LogBO logBO;

  @Inject
  JsonWebToken jwt;

  private final String REQUEST_TIME = "request-time";

  @Override
  public void filter(ContainerRequestContext requestContext) {
    requestContext.setProperty(REQUEST_TIME, LocalDateTime.now());
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    if (jwt == null || !jwt.containsClaim("userId")) return;

    String action = requestContext.getMethod() + " " + requestContext.getUriInfo().getPath();

    JsonNumber jsonNumber = jwt.getClaim("userId");
    long userId = jsonNumber.longValue();
    int status = responseContext.getStatus();
    LocalDateTime time = (LocalDateTime) requestContext.getProperty(REQUEST_TIME);

    logBO.create(new LogDTO(action, userId, status, time));
  }
}
