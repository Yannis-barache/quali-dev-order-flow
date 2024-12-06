package org.ormi.priv.tfa.orderflow.lib.event.sourcing.store;

import java.util.List;

/**
 * Event store.
 * 
 * The event store is responsible for storing and retrieving events.
 * 
 * @param <T> - the type of the event payload
 */
public interface EventStore<T> {
  /**
   * Save an event.
   * 
   * @param event - the event to save
   */
  public void saveEvent(T event);

  /**
   * Get events by aggregate root id.
   * 
   * @apiNote This method should return the events in the order they were created
   *          starting from the specified version. The provided version is
   *          supposed exclusive.
   * 
   * @param aggregateRootId - the aggregate root id
   * @return the list of ordered events
   */
  public List<T> findEventsByAggregateRootIdAndStartingVersion(String aggregateRootId, long startingVersion);
}
