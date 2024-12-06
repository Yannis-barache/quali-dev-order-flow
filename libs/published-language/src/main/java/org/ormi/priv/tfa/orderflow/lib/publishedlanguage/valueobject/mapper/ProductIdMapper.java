package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;

@Mapper
public class ProductIdMapper {

  @Named("toProductId")
  public ProductId toProductId(String id) {
    return ProductId.of(id);
  }

  @Named("productIdToString")
  public String productIdToString(ProductId productId) {
    return productId.getId();
  }
}
