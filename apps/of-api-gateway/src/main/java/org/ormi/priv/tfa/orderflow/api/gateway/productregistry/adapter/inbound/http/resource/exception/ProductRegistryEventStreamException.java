package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource.exception;

public class ProductRegistryEventStreamException extends RuntimeException {

  public ProductRegistryEventStreamException(String message) {
    super(message);
  }

  public ProductRegistryEventStreamException(String message, Throwable cause) {
    super(message, cause);
  }
}
