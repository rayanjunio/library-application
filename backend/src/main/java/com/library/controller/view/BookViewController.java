package com.library.controller.view;

import com.library.templates.Templates;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("books")
public class BookViewController {

    @GET
    @Path("view")
    @Produces(MediaType.TEXT_HTML)
    public Response userBooks(@Context SecurityContext context) {
        if (context.isUserInRole("ADMIN")) return Response.ok(Templates.livros_admin().render()).build();
        else return Response.ok(Templates.livros_user().render()).build();
    }
} 