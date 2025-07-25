package com.library.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.NotFoundException;

import java.io.InputStream;

@Provider
public class ErrorController implements jakarta.ws.rs.ext.ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        InputStream page = getClass().getClassLoader().getResourceAsStream("templates/404.html");
        if (page == null) {
            return Response.status(404).entity("404 - Página não encontrada").type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(404).entity(page).type(MediaType.TEXT_HTML).build();
    }
}
