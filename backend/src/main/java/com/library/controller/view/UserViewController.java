package com.library.controller.view;

import com.library.model.dto.auth.UserTokenInfoDTO;
import com.library.model.enums.Role;
import com.library.security.JwtContext;
import com.library.templates.Templates;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("users")
public class UserViewController {

    @Inject
    JwtContext jwtContext;

    @GET
    @Path("view")
    @RolesAllowed({ "ADMIN" })
    @Produces(MediaType.TEXT_HTML)
    public Response users() {
        return Response.ok(Templates.usuarios_admin().render()).build();
    }
} 