package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto;

public record UpdateProductCommandDto(
    String productId,
    String name,
    String description
) {
}
