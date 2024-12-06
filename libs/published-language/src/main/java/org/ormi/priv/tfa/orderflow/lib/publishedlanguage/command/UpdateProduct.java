package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Command to update a product in the registry.
 */
public final class UpdateProduct implements ProductRegistryCommand {
  /**
   * Serial version UID
   */
  private static final long serialVersionUID = 1L;

  /**
   * The product id
   */
  private ProductId productId;

  /**
   * The name of the product
   */
  private String name;

  /**
   * The description of the product
   */
  private String productDescription;

  /**
   * Constructor
   * 
   * @param productId          - The product id
   * @param name               - The name of the product
   * @param productDescription - The description of the product
   */
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public UpdateProduct(
      @JsonProperty("productId") ProductId productId,
      @JsonProperty("name") String name,
      @JsonProperty("productDescription") String productDescription) {
    this.productId = productId;
    this.name = name;
    this.productDescription = productDescription;
  }

  /**
   * Get the product id
   * 
   * @return The product id
   */
  public ProductId getProductId() {
    return productId;
  }

  /**
   * Get the product name
   * 
   * @return Name of the product
   */
  public String getName() {
    return name;
  }

  /**
   * Get the product description
   * 
   * @return Description of the product
   */
  public String getProductDescription() {
    return productDescription;
  }

  @Override
  public String toString() {
    return String.format("%s{productId='%s', name='%s', productDescription='%s'}", this.getClass().getSimpleName(),
        productId, name, productDescription);
  }
}
