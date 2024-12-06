package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource.exception.mapper;

import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource.exception.ProductRegistryEventStreamException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link ProductRegistryEventStreamException}.
 */
@Provider
public class ProductRegistryEventStreamExceptionMapper
    implements ExceptionMapper<ProductRegistryEventStreamException> {

  /**
   * Maps the exception to a server error response.
   */
  @Override
  public Response toResponse(ProductRegistryEventStreamException exception) {
    return Response.serverError().entity(exception.getMessage()).build();
  }
}
