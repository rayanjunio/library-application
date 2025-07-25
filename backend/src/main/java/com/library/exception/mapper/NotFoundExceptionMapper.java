package com.library.exception.mapper;

import com.library.templates.Templates;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.NotFoundException;

@Provider
public class NotFoundExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<NotFoundException> {

  @Override
  public Response toResponse(NotFoundException exception) {
    return Response.status(404)
            .entity(Templates.not_found().render())
            .type(MediaType.TEXT_HTML)
            .build();
  }
}