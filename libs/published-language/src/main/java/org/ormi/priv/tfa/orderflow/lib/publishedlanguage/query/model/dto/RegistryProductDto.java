package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.dto;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProductById.GetProductByIdResult;

public record RegistryProductDto(
  String id,
  String name,
  String description,
  Long updatedAt,
  Long registeredAt) implements GetProductByIdResult {
}
