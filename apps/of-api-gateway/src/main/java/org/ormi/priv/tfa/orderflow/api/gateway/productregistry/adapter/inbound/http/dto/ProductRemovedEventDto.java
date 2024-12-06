package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto;

public final class ProductRemovedEventDto extends ProductRegistryEventDto {
  public DtoPayload payload;

  public static final class DtoPayload implements ProductRegistryEventDto.DtoPayload {
    public String id;
  }
}
