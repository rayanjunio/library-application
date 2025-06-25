package com.library.controller;

import com.library.bo.UserBO;
import com.library.dto.request.user.UserRequestDTO;
import com.library.dto.request.user.UserUpdateDTO;
import com.library.dto.response.UserResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("users")
public class UserController {

  @Inject
  UserBO userBO;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(@Valid UserRequestDTO userRequestDTO) {
    try {
      UserResponseDTO response = userBO.createUser(userRequestDTO);
      return Response.status(Response.Status.CREATED).entity(response).build();
    } catch (RuntimeException e) {
      throw new RuntimeException(e);
    }
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({ "ADMIN", "MEMBER" })
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
  @RolesAllowed({ "ADMIN" })
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
  @RolesAllowed({ "ADMIN", "MEMBER" })
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("id") long id, @Valid UserUpdateDTO userUpdateDTO) {
    try {
      UserResponseDTO response = userBO.updateUser(id, userUpdateDTO);
      return Response.status(Response.Status.OK).entity(response).build();
    } catch (RuntimeException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({ "ADMIN", "MEMBER" })
  public Response deleteUser(@PathParam("id") long id) {
    try {
      userBO.deleteUser(id);
      return Response.status(Response.Status.NO_CONTENT).build();
    } catch (RuntimeException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }
}
