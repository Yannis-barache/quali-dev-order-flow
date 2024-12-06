package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event;

import org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate.Event;
import org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate.EventId;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event to indicate that a product has been registered.
 */
public final class ProductRegistered extends Event implements ProductRegistryEvent {
  private final static String EVENT_TYPE = "ProductRegistered";

  public static final class Payload {
    /**
     * The id of the product.
     */
    public ProductId productId;
    /**
     * The name of the product.
     */
    public final String name;
    /**
     * The description of the product.
     */
    public final String productDescription;

    public Payload(
      @JsonProperty("productId") ProductId productId,
      @JsonProperty("name") String name,
      @JsonProperty("productDescription") String productDescription) {
      this.productId = productId;
      this.name = name;
      this.productDescription = productDescription;
    }
  }

  /**
   * The payload for the event.
   */
  public Payload payload;

  /**
   * Constructor.
   * 
   * @param id - The id of the event.
   * @param aggregateId - The id of the aggregate.
   * @param version - The version of the aggregate.
   * @param timestamp - The timestamp of the event.
   * @param payload - The payload of the event.
   */
  public ProductRegistered(
      @JsonProperty("id") EventId id,
      @JsonProperty("aggregateId") String aggregateId,
      @JsonProperty("version") long version,
      @JsonProperty("timestamp") long timestamp,
      @JsonProperty("payload") Payload payload) {
    super(id, aggregateId, version, timestamp, EVENT_TYPE);
    this.payload = payload;
  }

  @Override
  public String toString() {
    return String.format("%s{productId=%s, name=%s, productDescription=%s}", this.getClass().getSimpleName(),
        payload.productId,
        payload.name,
        payload.productDescription);
  }

  @Override
  protected String getEventType() {
    return EVENT_TYPE;
  }
}
