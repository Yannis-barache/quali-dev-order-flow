package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.config;

public enum ProductRegistryQueryChannelName {
  PRODUCT_REGISTRY_READ_RESULT("product-registry-read-result");

  private final String value;

  ProductRegistryQueryChannelName(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
