package com.library.controller;

import com.library.model.bo.LoanBO;
import com.library.model.dto.loan.LoanRequestDTO;
import com.library.model.dto.loan.LoanResponseDTO;
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
      LoanResponseDTO response = loanBO.createLoan(loanRequestDTO);
      return Response.status(Response.Status.CREATED).entity(response).build();
  }

  @PUT
  @Path("{loanId}")
  @RolesAllowed( { "ADMIN" })
  public Response finishLoan(@PathParam("loanId") int loanId) {
      LoanResponseDTO response = loanBO.finishLoan(loanId);
      return Response.status(Response.Status.OK).entity(response).build();
  }

  @PATCH
  @Path("{email}")
  @RolesAllowed({ "ADMIN" })
  public Response removeUserFine(@PathParam("email") String email) {
      loanBO.removeUserFine(email);
      return Response.status(Response.Status.NO_CONTENT).build();
  }
}
