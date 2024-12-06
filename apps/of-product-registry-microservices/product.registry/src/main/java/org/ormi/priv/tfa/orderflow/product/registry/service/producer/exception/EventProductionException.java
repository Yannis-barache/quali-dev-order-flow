package org.ormi.priv.tfa.orderflow.product.registry.service.producer.exception;

public class EventProductionException extends RuntimeException {
  public EventProductionException(String message) {
    super(message);
  }

  public EventProductionException(String message, Throwable cause) {
    super(message,cause);
  }


}


