package org.ormi.priv.tfa.orderflow.product.registry.aggregate;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;

/**
 * Product Registry - Product entity
 *
 */
public class Product {
  /**
   * Universal system identifier for a product
   */
  private final ProductId productId;
  /**
   * Name of the product
   */
  private final String name;
  /**
   * Description of the product
   */
  private final String productDescription;

  /**
   * Constructor
   * 
   * @param productId - Universal system identifier for a product
   * @param name - Name of the product
   * @param productDescription - Description of the product
   */
  public Product(ProductId productId, String name, String productDescription) {
    this.productId = productId;
    this.name = name;
    this.productDescription = productDescription;
  }

  /**
   * Get the product identifier
   * 
   * @return Universal system identifier for a product
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
    return "Product{" +
      "productId=" + productId +
      ", name='" + name + '\'' +
      ", productDescription='" + productDescription + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Product)) return false;
    Product other = (Product) o;
    // Compare the product identifiers as it is the only intrinsic equality of an entity
    return productId.equals(other.productId);
  }

  @Override
  public int hashCode() {
    return productId.hashCode();
  }
}
