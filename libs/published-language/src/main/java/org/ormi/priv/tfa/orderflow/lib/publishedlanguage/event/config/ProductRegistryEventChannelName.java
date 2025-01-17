package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.config;

public enum ProductRegistryEventChannelName {
  PRODUCT_REGISTRY_EVENT_MESSAGE("product-registry-event-message");

  private final String value;

  ProductRegistryEventChannelName(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}