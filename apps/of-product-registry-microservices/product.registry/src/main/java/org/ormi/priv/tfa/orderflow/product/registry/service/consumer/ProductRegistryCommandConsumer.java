package org.ormi.priv.tfa.orderflow.product.registry.service.consumer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.ProductRegistryCommand;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.ProductRegistry;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.service.ProductRegistryService;
import org.ormi.priv.tfa.orderflow.product.registry.service.producer.ProductRegistryEventEmitter;

import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.pulsar.PulsarIncomingMessageMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Registry service to interact with the product registry and handle commands that are received
 */
@ApplicationScoped
public class ProductRegistryCommandConsumer {


  /**
   * The registry service to interact with the product registry.
   */
  @Inject
  private ProductRegistryService productRegistryService;

  /**
   * The event producer to produce events to the event store.
   */
  @Inject
  private ProductRegistryEventEmitter eventProducer;

  /**
   * The cached product registry.
   * 
   * @implNote This field is used to cache the product registry to avoid loading it from the event
   * @implNote Avoid accessing this field directly, use the {@link #loadRegistry()} method instead.
   */
  private ProductRegistry registry;

  /**
   * Get the cached product registry.
   * 
   * @implNote This method will load the product registry if it is not already loaded from the
   * event store.
   * @implNote This method automatically inject the product registry service into the product registry.
   * 
   * @return the product registry
   */
  public ProductRegistry loadRegistry() {
    if (registry == null) {
      registry = new ProductRegistry(productRegistryService);
    }
    return productRegistryService.loadProductRegistry(registry);
  }

  /**
   * Handle the product registry command.
   * 
   * @param msg - the message containing the command
   * @return a completion stage that indicates when the message has been processed
   * and the message has been acknowledged
   */
  @Incoming("product-registry-command")
  @Transactional
  public CompletionStage<Void> handleCommand(Message<ProductRegistryCommand> msg) {
    // Get the correlation id from the message metadata
    final var metadata = msg.getMetadata(PulsarIncomingMessageMetadata.class).orElseThrow();
    final String correlationId = Optional.ofNullable(metadata.getProperty("correlation-id")).orElseThrow();
    // Get the message and its payload
    final ProductRegistryCommand cmd = msg.getPayload();

    // Handle the command
    return loadRegistry().handle(cmd)
        .subscribeAsCompletionStage()
        .thenAccept(evt -> {
          // Produce event on correlated bus
          eventProducer.sink(correlationId, evt);
          Log.debug(String.format("Acknowledge command: %s", cmd.getClass().getName()));
          msg.ack();
        }).exceptionallyCompose(e -> {
          // Log error and nack message
          Log.error(String.format("Failed to handle command: %s", e.getMessage()));
          msg.nack(e);
          return CompletableFuture.failedFuture(e);
        });
  }
}
