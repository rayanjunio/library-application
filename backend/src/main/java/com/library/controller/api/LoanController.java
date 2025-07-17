package com.library.controller.api;

import com.library.model.bo.LoanBO;
import com.library.model.dto.auth.UserTokenInfoDTO;
import com.library.model.dto.loan.LoanRequestDTO;
import com.library.model.dto.loan.LoanResponseDTO;
import com.library.model.dto.loan.LoanUpdateDTO;
import com.library.security.JwtContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("loan")
public class LoanController {

  @Inject
  LoanBO loanBO;

  @Inject
  JwtContext jwtContext;

  @POST
  @Path("create")
  @RolesAllowed({ "ADMIN" })
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createLoan(@Valid LoanRequestDTO loanRequestDTO) {
    LoanResponseDTO response = loanBO.createLoan(loanRequestDTO);
    return Response.status(Response.Status.CREATED).entity(response).build();
  }

  @PUT
  @Path("update")
  @RolesAllowed( { "ADMIN" })
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateLoan(LoanUpdateDTO loanUpdateDTO) {
    LoanResponseDTO response = loanBO.updateLoan(loanUpdateDTO);
    return Response.status(Response.Status.OK).entity(response).build();
  }

  @DELETE
  @Path("delete/{loanId}")
  @RolesAllowed({ "ADMIN" })
  public Response deleteLoan(@PathParam("loanId") int loanId) {
    loanBO.deleteLoan(loanId);
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @PUT
  @Path("finish/{loanId}")
  @RolesAllowed( { "ADMIN" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response finishLoan(@PathParam("loanId") int loanId) {
      LoanResponseDTO response = loanBO.finishLoan(loanId);
      return Response.status(Response.Status.OK).entity(response).build();
  }

  @PATCH
  @Path("remove-fine/{email}")
  @RolesAllowed({ "ADMIN" })
  public Response removeUserFine(@PathParam("email") String email) {
      loanBO.removeUserFine(email);
      return Response.status(Response.Status.NO_CONTENT).build();
  }

  @GET
  @Path("get-from-user")
  @RolesAllowed({ "MEMBER" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUserActiveLoans() {
    UserTokenInfoDTO userinfo = jwtContext.getUserInfo();
    return Response.ok(Map.of("activeLoans", loanBO.getUserActiveLoans(userinfo.getId()))).build();
  }

  @GET
  @Path("get-all")
  @RolesAllowed({ "ADMIN" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getActiveLoans() {
    return Response.ok(Map.of("activeLoans", loanBO.getActiveLoans())).build();
  }

  @GET
  @Path("count-from-user")
  @RolesAllowed({ "MEMBER" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response countUserActiveLoans() {
    UserTokenInfoDTO userinfo = jwtContext.getUserInfo();
    return Response.ok(Map.of("activeLoansCount", loanBO.countUserActiveLoans(userinfo.getId()))).build();
  }

  @GET
  @Path("count-all")
  @RolesAllowed({ "ADMIN" })
  @Produces(MediaType.APPLICATION_JSON)
  public Response countAllActiveLoans() {
    return Response.ok(Map.of("activeLoansCount", loanBO.countAllActiveLoans())).build();
  }
}