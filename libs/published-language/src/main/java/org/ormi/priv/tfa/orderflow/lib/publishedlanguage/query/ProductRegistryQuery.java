package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProductById.GetProductByIdResult;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProducts.GetProductsResult;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GetProductById.class, name = "GetProductById"),
    @JsonSubTypes.Type(value = GetProducts.class, name = "GetProducts")
})
public sealed interface ProductRegistryQuery permits GetProductById, GetProducts {
  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "type"
  )
  @JsonSubTypes({
      @JsonSubTypes.Type(value = GetProductByIdResult.class, name = "GetProductByIdResult"),
      @JsonSubTypes.Type(value = GetProductsResult.class, name = "GetProductsResult")
  })
  public interface ProductRegistryQueryResult {
  }
}