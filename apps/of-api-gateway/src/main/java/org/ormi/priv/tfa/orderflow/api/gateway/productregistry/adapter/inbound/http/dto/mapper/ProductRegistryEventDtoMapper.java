package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductRegisteredEventDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductRemovedEventDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductUpdatedEventDto;
import org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate.mapper.EventIdMapper;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistered;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRemoved;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductUpdated;

@Mapper(uses = {EventIdMapper.class, ProductRegistryEventPayloadMapper.class})
public interface ProductRegistryEventDtoMapper {

  ProductRegistryEventDtoMapper INSTANCE = Mappers.getMapper(ProductRegistryEventDtoMapper.class);

  @Mapping(target = "id", source = "id", qualifiedByName = "eventIdToString")
  @Mapping(target = "productRegistryId", source = "aggregateId")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "eventType", source = "eventType")
  @Mapping(target = "payload", source = "payload",  qualifiedByName = "productRegisteredPayloadToDto")
  ProductRegisteredEventDto toDto(ProductRegistered evt);

  @Mapping(target = "id", source = "id", qualifiedByName = "eventIdToString")
  @Mapping(target = "productRegistryId", source = "aggregateId")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "eventType", source = "eventType")
  @Mapping(target = "payload", source = "payload",  qualifiedByName = "productUpdatedPayloadToDto")
  ProductUpdatedEventDto toDto(ProductUpdated evt);

  @Mapping(target = "id", source = "id", qualifiedByName = "eventIdToString")
  @Mapping(target = "productRegistryId", source = "aggregateId")
  @Mapping(target = "version", source = "version")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "eventType", source = "eventType")
  @Mapping(target = "payload", source = "payload",  qualifiedByName = "productRemovedPayloadToDto")
  ProductRemovedEventDto toDto(ProductRemoved evt);

  @Mapping(target = "errorMessage", source = "errorMessage")
  ProductRegistryErrorDto toDto(ProductRegisteryError error);
}
