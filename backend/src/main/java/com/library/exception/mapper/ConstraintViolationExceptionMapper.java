package com.library.exception.mapper;

import com.library.exception.model.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
  private final int STATUS = 400;

  @Override
  public Response toResponse(ConstraintViolationException exception) {
    List<String> errors = exception.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .toList();

    ErrorResponse response = new ErrorResponse(STATUS, String.join(", ", errors));
    return Response.status(STATUS).entity(response).build();
  }
}
