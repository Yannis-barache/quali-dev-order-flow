package org.ormi.priv.tfa.orderflow.product.registry.read.service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProductById;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProductById.GetProductByIdResult;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProducts;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProducts.GetProductsResult;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.ProductRegistryQuery;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.ProductNotFound;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.dto.RegistryProductDtoCollection;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;
import org.ormi.priv.tfa.orderflow.product.registry.read.adapter.outbound.message.dto.mapper.RegistryProductDtoMapper;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.pulsar.PulsarIncomingMessageMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The product query consumer.
 */
@ApplicationScoped
public class ProductQueryConsumer {

  /**
   * The product service.
   */
  @Inject
  private ProductService productService;

  /**
   * Query result emitter.
   */
  @Inject
  private ProductQueryResultEmitter resultEmitter;

  /**
   * Handle product queries.
   * 
   * @param qry the product query
   */
  @Incoming("product-registry-query")
  @Blocking
  public CompletionStage<Void> handleQuery(Message<ProductRegistryQuery> msg) {
    // Get the correlation id from the message metadata
    final var metadata = msg.getMetadata(PulsarIncomingMessageMetadata.class).orElseThrow();
    final String correlationId = Optional.ofNullable(metadata.getProperty("correlation-id")).orElseThrow();
    // Get the message and its payload
    final ProductRegistryQuery qry = msg.getPayload();

    return Uni.createFrom().deferred(() -> {
      if (qry instanceof GetProductById) {
        GetProductById getProductById = (GetProductById) qry;
        return getProductById(getProductById.getProductId());
      } else if (qry instanceof GetProducts) {
        return getAllProducts();
      }
      return Uni.createFrom().failure(new IllegalArgumentException("Unknown query type"));
    })
    .subscribeAsCompletionStage()
    .thenAccept(result -> {
      // Sink the result on correlated bus
      resultEmitter.sink(correlationId, result);
      msg.ack();
    }).exceptionallyCompose(e -> {
      // Log error and nack message
      Log.error("Failed to handle query", e);
      msg.nack(e);
      return CompletableFuture.failedFuture(e);
    });
  }

  /**
   * Get a product by id.
   * 
   * @param productId - the product id
   */
  public Uni<GetProductByIdResult> getProductById(ProductId productId) {
    return Uni.createFrom().item(() -> {
      final var product = productService.getProductById(productId);
      // Return the result
      if (product.isPresent()) {
        return RegistryProductDtoMapper.INSTANCE.toRegistryProductDto(product.get());
      }
      return new ProductNotFound(String.format("Product not found with id{%s}", productId));
    });
  }

  /**
   * Get all products.
   */
  public Uni<GetProductsResult> getAllProducts() {
    return Uni.createFrom().item(() -> {
      // Return the result
      return new RegistryProductDtoCollection(
          productService
              .getAllProducts()
              .stream()
              .map(RegistryProductDtoMapper.INSTANCE::toRegistryProductDto)
              .collect(Collectors.toList()));
    });
  }
}
