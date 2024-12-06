package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.RegisterProductCommandDto;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.RegisterProduct;

public class RegisterProductCommandDtoMapperTest {

  @Test
  public void toCommand_shouldMap_dto_toCommand() {
    // Given
    final String name = "product1";
    final String description = "this is a product";
    RegisterProductCommandDto dto = new RegisterProductCommandDto(name, description);

    // When
    RegisterProduct command = ProductRegistryCommandDtoMapper.INSTANCE.toCommand(dto);

    // Then
    assertEquals(name, command.getName());
    assertEquals(description, command.getProductDescription());
  }
}
