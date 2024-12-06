package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Command to remove a product from the registry.
 */
public final class RemoveProduct implements ProductRegistryCommand {
  /**
   * Serial version UID
   */
  private static final long serialVersionUID = 1L;

  /**
   * The product id
   */
  private final ProductId productId;

  /**
   * Constructor
   * 
   * @param productId - The product id
   */
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public RemoveProduct(@JsonProperty("productId") ProductId productId) {
    this.productId = productId;
  }

  /**
   * Get the product id
   * 
   * @return The product id
   */
  public ProductId getProductId() {
    return productId;
  }

  @Override
  public String toString() {
    return String.format("%s{productId=%s}", this.getClass().getSimpleName(), productId);
  }
}
