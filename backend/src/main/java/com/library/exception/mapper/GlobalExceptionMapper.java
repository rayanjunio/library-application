package com.library.exception.mapper;

import com.library.exception.model.ErrorResponse;
import com.library.model.bo.LogBO;
import com.library.model.dto.log.LogDTO;
import com.library.model.dto.log.RequestLogContextDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.time.LocalDateTime;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

  @Context
  ContainerRequestContext requestContext;

  @Inject
  RequestLogContextDTO context;

  @Inject
  ManagedExecutor managedExecutor;

  @Inject
  LogBO logBO;

  private final int STATUS = 500;
  private final String MESSAGE = "Ocorreu um erro inesperado";

  @Override
  public Response toResponse(Throwable throwable) {
    String path = requestContext != null ? requestContext.getUriInfo().getPath() : "unknown path";

    // Treat devtools requests
    if (path.contains(".well-known") || path.contains("devtools")) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    String action = "ERROR: " + throwable.getClass().getSimpleName() + " at " + context.getAction();

    managedExecutor.runAsync(() -> {
      logBO.create(new LogDTO(action, context.getUserId(), LocalDateTime.now()));
    });

    throwable.printStackTrace();
    ErrorResponse error = new ErrorResponse(STATUS, MESSAGE);
    return Response.status(STATUS).entity(error).build();
  }
}
