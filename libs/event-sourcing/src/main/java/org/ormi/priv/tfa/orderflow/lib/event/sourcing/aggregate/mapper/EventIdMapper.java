package org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate.EventId;

@Mapper
public class EventIdMapper {
  
  @Named("eventIdToString")
  public String eventIdToString(EventId eventId) {
    return eventId.getId();
  }

  @Named("toEventId")
  public EventId toEventId(String eventId) {
    return EventId.of(eventId);
  }
}
