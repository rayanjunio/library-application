package com.library.security;

import com.library.model.bo.LogBO;
import com.library.model.dto.log.LogDTO;
import com.library.model.dto.log.RequestLogContextDTO;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.context.ManagedExecutor;

@Provider
@Priority(20)
public class AuditResponseFilter implements ContainerResponseFilter {

  @Inject
  RequestLogContextDTO context;

  @Inject
  LogBO logBO;

  @Inject
  ManagedExecutor managedExecutor;

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    if (responseContext.getStatus() < 500) {
      String path = requestContext.getUriInfo().getPath();

      if(path.startsWith(".well-known") || path.contains("devtools") || path.contains("chrome")) {
        return;
      }

      String action = context.getAction();
      managedExecutor.runAsync(() -> {
        logBO.create(new LogDTO(action, context.getUserId(), context.getTimestamp()));
      });
    }
  }
}
