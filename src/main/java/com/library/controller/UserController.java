package com.library.controller;

import com.library.bo.UserBO;
import com.library.dto.request.UserRequestDTO;
import com.library.dto.response.UserResponseDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("users")
public class UserController {

  @Inject
  UserBO userBO;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(UserRequestDTO userRequestDTO) {
    try {
      UserResponseDTO response = userBO.createUser(userRequestDTO);
      return Response.status(Response.Status.CREATED).entity(response).build();
    } catch (RuntimeException e) {
      throw new RuntimeException(e);
    }
  }
}
