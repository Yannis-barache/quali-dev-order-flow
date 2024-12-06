package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto;

public record RegisterProductCommandDto(
    String name,
    String description) {
}
