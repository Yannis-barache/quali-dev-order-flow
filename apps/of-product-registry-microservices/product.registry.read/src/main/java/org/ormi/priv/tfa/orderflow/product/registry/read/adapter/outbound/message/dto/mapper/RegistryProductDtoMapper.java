package org.ormi.priv.tfa.orderflow.product.registry.read.adapter.outbound.message.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.dto.RegistryProductDto;
import org.ormi.priv.tfa.orderflow.product.registry.read.projection.repository.model.ProductEntity;

@Mapper
public interface RegistryProductDtoMapper {
  
  RegistryProductDtoMapper INSTANCE = Mappers.getMapper(RegistryProductDtoMapper.class);

  @Mapping(target = "id", source = "productId")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "updatedAt", source = "updatedAt")
  @Mapping(target = "registeredAt", source = "registeredAt")
  RegistryProductDto toRegistryProductDto(ProductEntity registryProduct);
}
