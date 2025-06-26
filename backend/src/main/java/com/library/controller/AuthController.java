package com.library.controller;

import com.library.service.AuthService;
import com.library.model.dto.auth.LoginRequestDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("auth")
public class AuthController {
  @Inject
  AuthService authService;

  @POST
  @Path(("login"))
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(LoginRequestDTO loginRequestDTO) {
      String token = authService.authenticate(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
      return Response.status(Response.Status.OK).entity("{\"token\":\"" + token + "\"}").build();
  }
}
