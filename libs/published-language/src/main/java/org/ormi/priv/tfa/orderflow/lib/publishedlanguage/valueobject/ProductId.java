package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject;

import java.util.UUID;

/**
 * Product Id value object
 */
public class ProductId {
  /**
   * Product Id internal value
   */
  private final String id;

  /**
   * Constructor.
   * 
   * Generate a new product id.
   * 
   * @implNote Use UUID to generate a new product id.
   */
  public ProductId() {
    this.id = UUID.randomUUID().toString();
  }

  /**
   * Constructor.
   * 
   * @param id the product id
   */
  private ProductId(String id) {
    this.id = id;
  }

  /**
   * Create a new instance of the given product id value.
   * 
   * @param id the product id
   */
  public static ProductId of(String id) throws IllegalArgumentException {
    try {
      UUID.fromString(id);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid product id. Must be a valid UUID.");
    }
    return new ProductId(id);
  }

  /**
   * Get the product id.
   * 
   * @return the product id
   */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return String.format("ProductId{value='%s'}", id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProductId)) return false;
    ProductId productId = (ProductId) o;
    return id.equals(productId.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
