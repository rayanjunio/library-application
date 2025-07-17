package com.library.controller.view;

import com.library.templates.Templates;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class PublicViewController {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response index() {
        return Response.ok(Templates.index().render()).build();
    }

    @GET
    @Path("login")
    @Produces(MediaType.TEXT_HTML)
    public Response login() {
        return Response.ok(Templates.index().render()).build();
    }

    @GET
    @Path("register")
    @Produces(MediaType.TEXT_HTML)
    public Response register() {
        return Response.ok(Templates.register().render()).build();
    }
} 