package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.dto.RegistryProductDtoCollection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Query to get all products.
 */
public final class GetProducts implements ProductRegistryQuery {
  /**
   * The associated result type of the query.
   */
  @JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
  )
  @JsonSubTypes({
    @JsonSubTypes.Type(value = RegistryProductDtoCollection.class, name = "RegistryProductDtoCollection")
  })
  public interface GetProductsResult extends ProductRegistryQueryResult {
  }

  /**
   * Create a new instance of the query.
   */
  public GetProducts() {
  }
}
