package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Command to register a product in the registry.
 */
public final class RegisterProduct implements ProductRegistryCommand {
  /**
   * Serial version UID
   */
  private static final long serialVersionUID = 1L;

  /**
   * The name of the product to register.
   */
  private String name;
  /**
   * The description of the product to register.
   */
  private String productDescription;

  /**
   * Constructor.
   * 
   * @param name               - The name of the product to register.
   * @param productDescription - The description of the product to register.
   */
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public RegisterProduct(
      @JsonProperty("name") String name,
      @JsonProperty("productDescription") String productDescription) {
    this.name = name;
    this.productDescription = productDescription;
  }

  /**
   * Get the name of the product to register.
   * 
   * @return the name of the product to register
   */
  public String getName() {
    return name;
  }

  /**
   * Get the description of the product to register.
   * 
   * @return the description of the product to register
   */
  public String getProductDescription() {
    return productDescription;
  }

  @Override
  public String toString() {
    return String.format("%s{name=%s, productDescription=%s}", this.getClass().getSimpleName(), name,
        productDescription);
  }
}
