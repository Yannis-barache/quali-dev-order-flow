package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.dto;

import java.util.List;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProducts.GetProductsResult;

public class RegistryProductDtoCollection implements GetProductsResult {
  private final List<RegistryProductDto> products;

  public RegistryProductDtoCollection(java.util.List<RegistryProductDto> products) {
    this.products = products;
  }

  public java.util.List<RegistryProductDto> getProducts() {
    return products;
  }
}
