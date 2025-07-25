package com.library.controller.api;

import com.library.model.bo.UserBO;
import com.library.model.dto.PagedResponseDTO;
import com.library.model.dto.user.ChangePasswordDTO;
import com.library.model.dto.user.UserRequestDTO;
import com.library.model.dto.user.UserUpdateDTO;
import com.library.model.dto.user.UserResponseDTO;
import com.library.service.AuthService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("user")
public class UserController {

  private static final int DEFAULT_PAGE_SIZE = 10;

  @Inject
  UserBO userBO;

  @Inject
  AuthService authService;

  @POST
  @Path("create")
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
  @Path("get")
  @RolesAllowed({ "ADMIN", "MEMBER" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUserWithLoans() {
      UserResponseDTO response = userBO.getUserWithLoans();
      return Response.status(Response.Status.OK).entity(response).build();
  }

  @GET
  @Path("get-all")
  @RolesAllowed({ "ADMIN" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllUsers(@QueryParam("page") @DefaultValue("0") int page) {
      PagedResponseDTO<UserResponseDTO> response = userBO.getAllUsers(page, DEFAULT_PAGE_SIZE);
      return Response.status(Response.Status.OK).entity(response).build();
  }

  @PUT
  @Path("update")
  @RolesAllowed({ "ADMIN", "MEMBER" })
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@Valid UserUpdateDTO userUpdateDTO) {
      UserResponseDTO response = userBO.updateUser(userUpdateDTO);
      return Response.status(Response.Status.OK).entity(response).build();
  }

  @DELETE
  @Path("delete")
  @RolesAllowed({ "ADMIN", "MEMBER" })
  public Response deleteAndLogoutUser() {
    userBO.deleteUser();
    NewCookie expiredCookie = authService.logout();
    return Response.status(Response.Status.NO_CONTENT).cookie(expiredCookie).build();
  }

  @PATCH
  @Path("set-password")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response changePassword(@Valid ChangePasswordDTO dto) {
      userBO.changePassword(dto);
      return Response.status(Response.Status.OK).build();
  }

  @GET
  @Path("count-all")
  @RolesAllowed({ "ADMIN" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response countAllUsers() {
    return Response.ok(Map.of("usersCount", userBO.countAllUsers())).build();
  }
}
