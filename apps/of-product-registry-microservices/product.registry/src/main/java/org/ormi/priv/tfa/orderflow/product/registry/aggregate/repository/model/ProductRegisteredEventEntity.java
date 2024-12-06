package org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model;

/**
 * Entity that represents a product registered event.
 *
 * @see ProductRegistryEventEntity
 */
public class ProductRegisteredEventEntity extends ProductRegistryEventEntity {
  static final String EVENT_TYPE = "ProductRegistered";

  /**
   * Payload for the event.
   */
  public static class Payload {
    /**
     * The id of the product.
     */
    public String productId;
    /**
     * The name of the product.
     */
    public String name;
    /**
     * The description of the product.
     */
    public String productDescription;
  }

  /**
   * The payload for the event.
   */
  public Payload payload;

  @Override
  public String getEventType() {
    return EVENT_TYPE;
  }
}
