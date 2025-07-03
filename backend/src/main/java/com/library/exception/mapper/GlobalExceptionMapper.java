package com.library.exception.mapper;

import com.library.exception.model.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
  private final int STATUS = 500;
  private final String MESSAGE = "An unexpected error occurred";

  @Override
  public Response toResponse(Throwable throwable) {
    exception.printStackTrace();
    ErrorResponse error = new ErrorResponse(STATUS, MESSAGE);
    return Response.status(STATUS).entity(error).build();
  }
}
