package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model;

public class NotFound {
  private final String message;

  public NotFound(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
