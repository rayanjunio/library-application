package com.library.security;

import com.library.exception.type.BusinessException;
import com.library.model.bo.UserBO;
import com.library.service.AuthService;
import com.library.templates.Templates;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Provider
@Priority(Priorities.AUTHENTICATION + 1)
public class UserExistenceFilter implements ContainerRequestFilter {

  @Inject
  UserBO userBO;

  @Inject
  JsonWebToken jwt;

  @Inject
  AuthService authService;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String path = requestContext.getUriInfo().getPath();

    // Public paths
    if (path.equals("/") || path.startsWith("login") || path.startsWith("register")) return;

    if (jwt.getClaim("userId") != null) {
      try {
        userBO.getUser();
      } catch (BusinessException ex) {
        if (ex.getStatus() == 404) {
          NewCookie expiredCookie = authService.logout();

          requestContext.abortWith(
                  Response.status(Response.Status.UNAUTHORIZED)
                          .cookie(expiredCookie)
                          .entity(Templates.unauthorized().render())
                          .type(MediaType.TEXT_HTML)
                          .build()
          );
        }
      }
    }
  }
}