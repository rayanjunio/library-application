package com.library.controller;

import com.library.bo.ProfileBO;
import com.library.dto.request.ProfileRequestDTO;
import com.library.dto.response.ProfileResponseDTO;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("profile")
public class ProfileController {
  @Inject
  ProfileBO profileBO;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createProfile(@Valid ProfileRequestDTO profileRequestDTO) {
    try {
      ProfileResponseDTO response = profileBO.createProfile(profileRequestDTO);
      return Response.status(Response.Status.CREATED).entity(response).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
    }
  }
}
