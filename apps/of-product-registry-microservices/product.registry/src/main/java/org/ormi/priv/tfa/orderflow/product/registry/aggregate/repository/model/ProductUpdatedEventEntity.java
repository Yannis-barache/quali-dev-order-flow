package org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model;

/**
 * The entity for the product updated event in the Mongo database.
 *
 * @see ProductRegistryEventEntity
 */
public class ProductUpdatedEventEntity extends ProductRegistryEventEntity {
  static final String EVENT_TYPE = "ProductUpdated";

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
