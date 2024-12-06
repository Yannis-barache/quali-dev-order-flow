package org.ormi.priv.tfa.orderflow.product.registry.service.producer.exception;

public class ProducerCloseException extends RuntimeException {

  public ProducerCloseException(String message) {
    super(message);
  }

  public ProducerCloseException(String message, Throwable cause) {
    super(message, cause);
  }
}
