package com.library.controller;

import com.library.bo.LoanBO;
import com.library.dto.request.loan.LoanRequestDTO;
import com.library.dto.response.LoanResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("loan")
public class LoanController {

  @Inject
  LoanBO loanBO;

  @POST
  @RolesAllowed({ "ADMIN" })
  public Response createLoan(@Valid LoanRequestDTO loanRequestDTO) {
    try {
      LoanResponseDTO response = loanBO.createLoan(loanRequestDTO);
      return Response.status(Response.Status.CREATED).entity(response).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
    }
  }

  @PUT
  @Path("{loanId}")
  @RolesAllowed( { "ADMIN" })
  public Response finishLoan(@PathParam("loanId") int loanId) {
    try {
      LoanResponseDTO response = loanBO.finishLoan(loanId);
      return Response.status(Response.Status.OK).entity(response).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
    }
  }

  @PATCH
  @Path("{email}")
  @RolesAllowed({ "ADMIN" })
  public Response removeUserFine(@PathParam("email") String email) {
    try {
      loanBO.removeUserFine(email);
      return Response.status(Response.Status.NO_CONTENT).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
    }
  }
}
