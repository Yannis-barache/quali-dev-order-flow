package org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate;

/**
 * Event base class.
 */
public abstract class Event {
  public final EventId id;
  public final String aggregateId;
  public final long version;
  public final long timestamp;
  public final String eventType;

  /**
   * Constructor.
   * 
   * @param id - the event id
   * @param aggregateId - the aggregate id
   * @param version - the version
   * @param timestamp - the timestamp
   * @param eventType - the event type
   * @param payload - the event payload
   */
  public Event(
      EventId id,
      String aggregateId,
      long version,
      long timestamp,
      String eventType) {
    this.id = id;
    this.aggregateId = aggregateId;
    this.version = version;
    this.timestamp = timestamp;
    this.eventType = getEventType();
  }

  protected abstract String getEventType();

  @Override
  public String toString() {
    return String.format("%s{}", this.getClass().getSimpleName());
  }
}
