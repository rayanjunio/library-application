package com.library.controller;

import com.library.bo.UserBO;
import com.library.dto.request.UserRequestDTO;
import com.library.dto.request.UserUpdateDTO;
import com.library.dto.response.UserResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("users")
public class UserController {

  @Inject
  UserBO userBO;

  @Inject
  JsonWebToken jwt;

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

  @GET
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "MEMBER"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUser(@PathParam("id") long id) {
    try {
      UserResponseDTO response = userBO.getUser(id);
      return Response.status(Response.Status.OK).entity(response).build();
    } catch (RuntimeException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @GET
  @RolesAllowed({"ADMIN"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllUsers() {
    try {
      List<UserResponseDTO> response = userBO.getAllUsers();
      return Response.status(Response.Status.OK).entity(response).build();
    } catch (RuntimeException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "MEMBER"})
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("id") long id, UserUpdateDTO userUpdateDTO) {
    try {
      long userId = Long.parseLong(jwt.getClaim("userId").toString());
      UserResponseDTO response = userBO.updateUser(id, userUpdateDTO, userId);
      return Response.status(Response.Status.OK).entity(response).build();
    } catch (RuntimeException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({"ADMIN", "MEMBER"})
  public Response deleteUser(@PathParam("id") long id) {
    try {
      long userId = Long.parseLong(jwt.getClaim("userId").toString());
      userBO.deleteUser(id, userId);
      return Response.status(Response.Status.NO_CONTENT).build();
    } catch (RuntimeException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }
}
