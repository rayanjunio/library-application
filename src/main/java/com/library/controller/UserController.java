package com.library.controller;

import com.library.model.bo.UserBO;
import com.library.model.dto.user.ChangePasswordDTO;
import com.library.model.dto.user.UserRequestDTO;
import com.library.model.dto.user.UserUpdateDTO;
import com.library.model.dto.user.UserResponseDTO;
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
      UserResponseDTO response = userBO.createUser(userRequestDTO);
      return Response.status(Response.Status.CREATED).entity(response).build();
  }

  @POST
  @Path("add-admin")
  @RolesAllowed({ "ADMIN" })
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createAdminUser(@Valid UserRequestDTO userRequestDTO) {
      UserResponseDTO response = userBO.createAdminUser(userRequestDTO);
      return Response.status(Response.Status.CREATED).entity(response).build();
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({ "ADMIN", "MEMBER" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUser(@PathParam("id") long id) {
      UserResponseDTO response = userBO.getUser(id);
      return Response.status(Response.Status.OK).entity(response).build();
  }

  @GET
  @RolesAllowed({ "ADMIN" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllUsers() {
      List<UserResponseDTO> response = userBO.getAllUsers();
      return Response.status(Response.Status.OK).entity(response).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed({ "ADMIN", "MEMBER" })
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("id") long id, @Valid UserUpdateDTO userUpdateDTO) {
      UserResponseDTO response = userBO.updateUser(id, userUpdateDTO);
      return Response.status(Response.Status.OK).entity(response).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed({ "ADMIN", "MEMBER" })
  public Response deleteUser(@PathParam("id") long id) {
      userBO.deleteUser(id);
      return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PATCH
  @Path("set-password")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response changePassword(@Valid ChangePasswordDTO dto) {
      userBO.changePassword(dto);
      return Response.status(Response.Status.OK).build();
  }
}
