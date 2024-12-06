package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.ProductNotFound;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.dto.RegistryProductDto;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Query to get product by id.
 */
public final class GetProductById implements ProductRegistryQuery {
  /**
   * The associated result type of the query.
   */
  @JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
  )
  @JsonSubTypes({
    @JsonSubTypes.Type(value = RegistryProductDto.class, name = "RegistryProductDto"),
    @JsonSubTypes.Type(value = ProductNotFound.class, name = "ProductNotFound")
  })
  public interface GetProductByIdResult extends ProductRegistryQueryResult {
  }

  /**
   * Product id to base the query on.
   */
  private final ProductId productId;

  /**
   * Create a new instance of the query.
   * 
   * @param productId the product id
   */
  public GetProductById(
    @JsonProperty("productId") ProductId productId) {
    this.productId = productId;
  }

  /**
   * Get the product id.
   * 
   * @return the product id
   */
  public ProductId getProductId() {
    return productId;
  }
}
