package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto;

public abstract class ProductRegistryEventDto {
  public String id;
  public String productRegistryId;
  public long version;
  public long timestamp;
  public String eventType;
  public DtoPayload payload;

  public static interface DtoPayload {
  }
}
