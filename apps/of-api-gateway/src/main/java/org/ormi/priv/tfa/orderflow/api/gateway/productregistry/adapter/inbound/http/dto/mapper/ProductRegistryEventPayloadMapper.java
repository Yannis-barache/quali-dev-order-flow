package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductRegisteredEventDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductRemovedEventDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductUpdatedEventDto;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistered;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRemoved;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductUpdated;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.mapper.ProductIdMapper;

@Mapper(uses = {ProductIdMapper.class})
public interface ProductRegistryEventPayloadMapper {
  
  @Named("productRegisteredPayloadToDto")
  @Mapping(target = "id", source = "productId", qualifiedByName = "productIdToString")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "productDescription")
  ProductRegisteredEventDto.DtoPayload toDto(ProductRegistered.Payload payload);

  @Named("productUpdatedPayloadToDto")
  @Mapping(target = "id", source = "productId", qualifiedByName = "productIdToString")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "productDescription")
  ProductUpdatedEventDto.DtoPayload toDto(ProductUpdated.Payload payload);

  @Named("productRemovedPayloadToDto")
  @Mapping(target = "id", source = "productId", qualifiedByName = "productIdToString")
  ProductRemovedEventDto.DtoPayload toDto(ProductRemoved.Payload payload);
}
