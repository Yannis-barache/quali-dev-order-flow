package org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate;

import java.util.UUID;

/**
 * Event Id value object
 */
public class EventId {
  /**
   * Event Id internal value
   */
  private final String id;

  /**
   * Constructor.
   * 
   * Generate a new event id.
   * 
   * @implNote Use UUID to generate a new event id.
   */
  public EventId() {
    this.id = UUID.randomUUID().toString();
  }

  /**
   * Constructor.
   * 
   * @param id the event id
   */
  private EventId(String id) {
    this.id = id;
  }

  /**
   * Create a new instance of the given event id value.
   * 
   * @param id the event id
   */
  public static EventId of(String id) {
    try {
      UUID.fromString(id);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid event id. Must be a valid UUID.");
    }
    return new EventId(id);
  }

  /**
   * Get the event id.
   * 
   * @return the event id
   */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return String.format("EventId{value='%s'}", id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EventId)) return false;
    EventId eventId = (EventId) o;
    return id.equals(eventId.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
