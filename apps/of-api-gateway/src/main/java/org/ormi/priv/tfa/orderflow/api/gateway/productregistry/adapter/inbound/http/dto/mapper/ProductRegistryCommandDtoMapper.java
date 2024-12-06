package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.RegisterProductCommandDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.RemoveProductCommandDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.UpdateProductCommandDto;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.RegisterProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.RemoveProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.UpdateProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.mapper.ProductIdMapper;

@Mapper(uses = {ProductIdMapper.class})
public interface ProductRegistryCommandDtoMapper {

  ProductRegistryCommandDtoMapper INSTANCE = Mappers.getMapper(ProductRegistryCommandDtoMapper.class);

  @Mapping(target = "name", source = "name")
  @Mapping(target = "productDescription", source = "description")
  RegisterProduct toCommand(RegisterProductCommandDto dto);

  @Mapping(target = "productId", source = "productId", qualifiedByName = "toProductId")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "productDescription", source = "description")
  UpdateProduct toCommand(UpdateProductCommandDto dto);

  @Mapping(target = "productId", source = "productId", qualifiedByName = "toProductId")
  RemoveProduct toCommand(RemoveProductCommandDto dto);
}
