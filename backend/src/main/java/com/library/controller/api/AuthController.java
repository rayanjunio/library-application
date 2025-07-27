package com.library.controller.api;

import com.library.service.AuthService;
import com.library.model.dto.auth.LoginRequestDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("auth")
public class AuthController {
  @Inject
  AuthService authService;

  @POST
  @Path(("login"))
  @RolesAllowed({ "ADMIN", "MEMBER" })
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(LoginRequestDTO loginRequestDTO) {
      NewCookie jwtCookie = authService.authenticate(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
      return Response.status(Response.Status.NO_CONTENT).cookie(jwtCookie).build();
  }

  @POST
  @Path(("logout"))
  @RolesAllowed({ "ADMIN", "MEMBER" })
  public Response logout() {
    NewCookie expiredCookie = authService.logout();
    return Response.status(Response.Status.OK).cookie(expiredCookie).build();
  }
}
