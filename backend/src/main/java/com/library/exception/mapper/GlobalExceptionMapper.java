package com.library.exception.mapper;

import com.library.exception.model.ErrorResponse;
import com.library.model.bo.LogBO;
import com.library.model.dto.log.LogDTO;
import com.library.model.dto.log.RequestLogContextDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.time.LocalDateTime;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

  @Inject
  RequestLogContextDTO context;

  @Inject
  ManagedExecutor managedExecutor;

  @Inject
  LogBO logBO;

  private final int STATUS = 500;
  private final String MESSAGE = "An unexpected error occurred";

  @Override
  public Response toResponse(Throwable throwable) {
    String action = "ERROR: " + throwable.getClass().getSimpleName() + " at " + context.getAction();

    managedExecutor.runAsync(() -> {
      logBO.create(new LogDTO(action, context.getUserId(), LocalDateTime.now()));
    });

    throwable.printStackTrace();
    ErrorResponse error = new ErrorResponse(STATUS, MESSAGE);
    return Response.status(STATUS).entity(error).build();
  }
}
