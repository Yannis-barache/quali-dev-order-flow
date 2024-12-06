package org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate.mapper.EventIdMapper;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistered;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRemoved;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductUpdated;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model.ProductRegisteredEventEntity;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model.ProductRemovedEventEntity;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model.ProductUpdatedEventEntity;


@Mapper(uses = {EventIdMapper.class, ProductRegistryEventPayloadMapper.class})
public interface ProductRegistryEventEntityMapper {

  ProductRegistryEventEntityMapper INSTANCE = Mappers.getMapper(ProductRegistryEventEntityMapper.class);

  /**
   * Maps a ProductRegistered event to a ProductRegisteredEventEntity.
   *
   * @param evt the ProductRegistered event
   * @return the mapped ProductRegisteredEventEntity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "eventId", source = "id", qualifiedByName = "eventIdToString")
  @Mapping(target = "eventType", source = "eventType")
  @Mapping(target = "aggregateRootId", source = "aggregateId")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "payload", source = "payload", qualifiedByName = "productRegisteredEventPayloadToEntity")
  ProductRegisteredEventEntity toEntity(ProductRegistered evt);

  /**
   * Maps a ProductRegisteredEventEntity to a ProductRegistered event.
   *
   * @param entity the ProductRegisteredEventEntity
   * @return the mapped ProductRegistered event
   */
  @Mapping(target = "id", source = "eventId", qualifiedByName = "toEventId")
  @Mapping(target = "eventType", ignore = true)
  @Mapping(target = "aggregateId", source = "aggregateRootId")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "payload", source = "payload", qualifiedByName = "productRegisteredEventPayloadToEvent")
  ProductRegistered toEvent(ProductRegisteredEventEntity entity);

  /**
   * Maps a ProductUpdated event to a ProductUpdatedEventEntity.
   *
   * @param evt the ProductUpdated event
   * @return the mapped ProductUpdatedEventEntity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "eventId", source = "id", qualifiedByName = "eventIdToString")
  @Mapping(target = "eventType", source = "eventType")
  @Mapping(target = "aggregateRootId", source = "aggregateId")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "payload", source = "payload", qualifiedByName = "productUpdatedEventEntityToEntity")
  ProductUpdatedEventEntity toEntity(ProductUpdated evt);

  /**
   * Maps a ProductUpdatedEventEntity to a ProductUpdated event.
   *
   * @param entity the ProductUpdatedEventEntity
   * @return the mapped ProductUpdated event
   */
  @Mapping(target = "id", source = "eventId", qualifiedByName = "toEventId")
  @Mapping(target = "eventType", ignore = true)
  @Mapping(target = "aggregateId", source = "aggregateRootId")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "payload", source = "payload", qualifiedByName = "productUpdatedEventPayloadToEvent")
  ProductUpdated toEvent(ProductUpdatedEventEntity entity);

  /**
   * Maps a ProductRemoved event to a ProductRemovedEventEntity.
   *
   * @param evt the ProductRemoved event
   * @return the mapped ProductRemovedEventEntity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "eventId", source = "id", qualifiedByName = "eventIdToString")
  @Mapping(target = "eventType", source = "eventType")
  @Mapping(target = "aggregateRootId", source = "aggregateId")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "payload", source = "payload", qualifiedByName = "productRemovedEventPayloadToEntity")
  ProductRemovedEventEntity toEntity(ProductRemoved evt);

  /**
   * Maps a ProductRemovedEventEntity to a ProductRemoved event.
   *
   * @param entity the ProductRemovedEventEntity
   * @return the mapped ProductRemoved event
   */
  @Mapping(target = "id", source = "eventId", qualifiedByName = "toEventId")
  @Mapping(target = "eventType", ignore = true)
  @Mapping(target = "aggregateId", source = "aggregateRootId")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "payload", source = "payload", qualifiedByName = "productRemovedEventPayloadToEvent")
  ProductRemoved toEvent(ProductRemovedEventEntity entity);
}