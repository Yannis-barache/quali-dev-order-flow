package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProductById.GetProductByIdResult;

public class ProductNotFound extends NotFound implements GetProductByIdResult {
  public ProductNotFound(String message) {
    super(message);
  }
}
