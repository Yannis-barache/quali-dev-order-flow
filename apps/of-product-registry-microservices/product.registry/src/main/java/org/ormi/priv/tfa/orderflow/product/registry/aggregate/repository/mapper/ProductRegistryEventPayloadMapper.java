package org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistered;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRemoved;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductUpdated;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.mapper.ProductIdMapper;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model.ProductRegisteredEventEntity;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model.ProductRemovedEventEntity;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model.ProductUpdatedEventEntity;

/**
 * Interface to map the events to the entities
 */
@Mapper(uses = {ProductIdMapper.class})
public interface ProductRegistryEventPayloadMapper {
  /**
   * Maps a ProductRegistered payload to a ProductRegisteredEventEntity.
   *
   * @param eventPayload the ProductRegistered payload to map to the entity
   * @return the mapped ProductRegisteredEventEntity payload
   */
  @Named("productRegisteredEventPayloadToEntity")
  @Mapping(target = "productId", source = "productId", qualifiedByName = "productIdToString")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "productDescription", source = "productDescription")
  public ProductRegisteredEventEntity.Payload toEntity(ProductRegistered.Payload eventPayload);

  /**
   * Maps a ProductRegisteredEventEntity payload to a ProductRegistered.
   *
   * @param entityPayload the ProductRegisteredEventEntity payload
   * @return the mapped ProductRegistered payload
   */
  @Named("productRegisteredEventPayloadToEvent")
  @Mapping(target = "productId", source = "productId", qualifiedByName = "toProductId")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "productDescription", source = "productDescription")
  public ProductRegistered.Payload toEvent(ProductRegisteredEventEntity.Payload entityPayload);

  /**
   * Maps a ProductUpdated payload to a ProductUpdatedEventEntity payload.
   *
   * @param eventPayload the ProductUpdated payload
   * @return the mapped ProductUpdatedEventEntity payload
   */
  @Named("productUpdatedEventEntityToEntity")
  @Mapping(target = "productId", source = "productId", qualifiedByName = "productIdToString")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "productDescription", source = "productDescription")
  public ProductUpdatedEventEntity.Payload toDto(ProductUpdated.Payload eventPayload);
  /**
   * Maps a ProductUpdatedEventEntity payload to a ProductUpdated payload.
   *
   * @param entityPayload the ProductUpdatedEventEntity payload
   * @return the mapped ProductUpdated payload
   */
  @Named("productUpdatedEventPayloadToEvent")
  @Mapping(target = "productId", source = "productId", qualifiedByName = "toProductId")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "productDescription", source = "productDescription")
  public ProductUpdated.Payload toEntity(ProductUpdatedEventEntity.Payload entityPayload);

  /**
   * Maps a ProductRemoved payload to a ProductRemovedEventEntity payload.
   *
   * @param eventPayload the ProductRemoved payload
   * @return the mapped ProductRemovedEventEntity payload
   */
  @Named("productRemovedEventPayloadToEntity")
  @Mapping(target = "productId", source = "productId", qualifiedByName = "productIdToString")
  public ProductRemovedEventEntity.Payload toEntity(ProductRemoved.Payload eventPayload);

  /**
   * Maps a ProductRemovedEventEntity payload to a ProductRemoved payload.
   *
   * @param entityPayload the ProductRemovedEventEntity payload
   * @return the mapped ProductRemoved payload
   */
  @Named("productRemovedEventPayloadToEvent")
  @Mapping(target = "productId", source = "productId", qualifiedByName = "toProductId")
  public ProductRemoved.Payload toEvent(ProductRemovedEventEntity.Payload entityPayload);
}
