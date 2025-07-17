package com.library.controller.view;

import com.library.templates.Templates;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("loans")
public class LoanViewController {

    @GET
    @Path("view")
    @Produces(MediaType.TEXT_HTML)
    public Response userLoans(@Context SecurityContext context) {
        if (context.isUserInRole("ADMIN")) return Response.ok(Templates.emprestimos_admin().render()).build();
        else return Response.ok(Templates.emprestimos_user().render()).build();
    }
} 