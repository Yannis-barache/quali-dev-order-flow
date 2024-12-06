package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event;

import org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate.Event;
import org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate.EventId;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event to indicate that a product has been updated
 */
public final class ProductUpdated extends Event implements ProductRegistryEvent {
  private final static String EVENT_TYPE = "ProductUpdated";

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
   * @param id - the event id
   * @param aggregateId - the aggregate id
   * @param version - the version
   * @param timestamp - the timestamp
   * @param payload - the payload
   */
  public ProductUpdated(
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
