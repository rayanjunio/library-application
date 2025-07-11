package com.library.security;

import com.library.model.bo.LogBO;
import com.library.model.dto.log.LogDTO;
import com.library.model.dto.log.RequestLogContextDTO;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.Priority;
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
@Priority(1)
@Blocking
public class AuditFilter implements ContainerRequestFilter, ContainerResponseFilter {

  @Inject
  RequestLogContextDTO context;

  @Inject
  LogBO logBO;

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

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    if (responseContext.getStatus() < 500) {
      String action = context.getAction();
      logBO.create(new LogDTO(action, context.getUserId(), context.getTimestamp()));
    }
  }
}
