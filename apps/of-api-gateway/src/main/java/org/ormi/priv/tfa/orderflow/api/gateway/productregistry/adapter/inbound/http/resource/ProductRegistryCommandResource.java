package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.resteasy.reactive.RestStreamElementType;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductRegisteredEventDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductRemovedEventDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductUpdatedEventDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.RegisterProductCommandDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.RemoveProductCommandDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.UpdateProductCommandDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.mapper.ProductRegistryCommandDtoMapper;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.mapper.ProductRegistryEventDtoMapper;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource.exception.ProductRegistryEventStreamException;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.ProductRegistryCommand;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.RegisterProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.RemoveProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.UpdateProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistered;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistryEvent;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRemoved;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductUpdated;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.config.ProductRegistryEventChannelName;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.pulsar.PulsarClientService;
import io.smallrye.reactive.messaging.pulsar.PulsarOutgoingMessage;
import io.smallrye.reactive.messaging.pulsar.PulsarOutgoingMessageMetadata;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/product/registry")
public class ProductRegistryCommandResource {

  /**
   * Pulsar client service for creating consumers and producers.
   */
  @Inject
  PulsarClientService pulsarClients;

  /**
   * Static emitter for sending product registry commands.
   */
  @Inject
  @Channel("product-registry-command")
  Emitter<ProductRegistryCommand> commandEmitter;

  /**
   * Endpoint to register a product.
   * 
   * @param cmdDto - DTO containing the product details
   * @return Response indicating the product registration was accepted
   */
  @POST
  @Path("/registerProduct")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerProduct(RegisterProductCommandDto cmdDto, @Context UriInfo uriInfo) {
    final RegisterProduct registerProduct = ProductRegistryCommandDtoMapper.INSTANCE.toCommand(cmdDto);
    final String correlationId = UUID.randomUUID().toString();
    commandEmitter.send(
        PulsarOutgoingMessage.from(Message.of(registerProduct))
            .addMetadata(PulsarOutgoingMessageMetadata.builder()
                .withProperties(Map.of("correlation-id", correlationId))
                .build()));
    return Response
        .seeOther(
            uriInfo.getBaseUriBuilder()
                .path(this.getClass())
                .path("/events/productRegistered")
                .queryParam("correlationId", correlationId)
                .build())
        .build();
  }

  /**
   * Endpoint to stream product registry registered events.
   * 
   * @param correlationId - correlation id to use for the consumer
   * @return Multi of product registry events
   */
  @GET
  @Path("/events/productRegistered")
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<ProductRegisteredEventDto> registeredEventStream(@QueryParam("correlationId") String correlationId) {
    // Create a stream of product registry events
    return Multi.createFrom().emitter(em -> {
      // Create consumer for product registry events with the given correlation id
      final Consumer<ProductRegistryEvent> consumer = getEventsConsumerByCorrelationId(correlationId);
      // Close the consumer on termination
      em.onTermination(() -> {
        try {
          consumer.unsubscribe();
        } catch (PulsarClientException e) {
          Log.error("Failed to close consumer for product registry events.", e);
        }
      });
      // Consume events and emit DTOs
      CompletableFuture.runAsync(() -> {
        while(!em.isCancelled()) {
          try {
            final var timeout = 10000;
            final var msg = Optional.ofNullable(consumer.receive(timeout, TimeUnit.MILLISECONDS));
            if (msg.isEmpty()) {
              // Complete the emitter if no event is received within the timeout. Free up resources.
              Log.debug("No event received within timeout of " + timeout + " seconds.");
              em.complete();
            }
            final ProductRegistryEvent evt = msg.get().getValue();
            Log.debug("Received event: " + evt);
            // Map event to DTO
            if (evt instanceof ProductRegistered registered) {
              Log.debug("Emitting DTO for registered event: " + registered);
              // Emit DTO for registered event
              em.emit(ProductRegistryEventDtoMapper.INSTANCE.toDto(registered));
            } else {
              // Fail the stream on unexpected event types
              Throwable error = new ProductRegistryEventStreamException("Unexpected event type: " + evt.getClass().getName());
              em.fail(error);
              return;
            }
            // Acknowledge the message
            consumer.acknowledge(msg.get());
          } catch (PulsarClientException e) {
            Log.error("Failed to receive event from consumer.", e);
            em.fail(e);
            return;
          }
        }
      });
    });
  }

  /**
   * Endpoint to update a product.
   * 
   * @param updateProduct - DTO containing the product details
   * @param uriInfo       - URI info for building the response URI
   * @return Response indicating the product update was accepted
   */
  @POST
  @Path("/updateProduct")
  @Consumes("application/json")
  public Response updateProduct(UpdateProductCommandDto updateProduct, @Context UriInfo uriInfo) {
    final UpdateProduct updateProductCommand = ProductRegistryCommandDtoMapper.INSTANCE.toCommand(updateProduct);
    final String correlationId = UUID.randomUUID().toString();
    commandEmitter.send(
        PulsarOutgoingMessage.from(Message.of(updateProductCommand))
            .addMetadata(PulsarOutgoingMessageMetadata.builder()
                .withProperties(Map.of("correlation-id", correlationId))
                .build()));
    return Response
        .seeOther(
            uriInfo.getBaseUriBuilder()
                .path(this.getClass())
                .path("/events/updated?correlationId=" + correlationId)
                .build())
        .build();
  }

  /**
   * Endpoint to stream product registry updated events.
   * 
   * @param correlationId - correlation id to use for the consumer
   * @return Multi of product registry events
   */
  @GET
  @Path("/events/productUpdated")
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<ProductUpdatedEventDto> updatedEventStream(@QueryParam("correlationId") String correlationId) {
    // Create a stream of product registry events
    return Multi.createFrom().emitter(em -> {
      // Create consumer for product registry events with the given correlation id
      final Consumer<ProductRegistryEvent> consumer = getEventsConsumerByCorrelationId(correlationId);
      // Close the consumer on termination
      em.onTermination(() -> {
        try {
          consumer.unsubscribe();
        } catch (PulsarClientException e) {
          Log.error("Failed to close consumer for product registry events.", e);
        }
      });
      // Consume events and emit DTOs
      CompletableFuture.runAsync(() -> {
        while(!em.isCancelled()) {
          try {
            final var timeout = 10000;
            final var msg = Optional.ofNullable(consumer.receive(timeout, TimeUnit.MILLISECONDS));
            if (msg.isEmpty()) {
              // Complete the emitter if no event is received within the timeout. Free up resources.
              Log.debug("No event received within timeout of " + timeout + " seconds.");
              em.complete();
            }
            final ProductRegistryEvent evt = msg.get().getValue();
            Log.debug("Received event: " + evt);
            // Map event to DTO
            if (evt instanceof ProductUpdated updated) {
              Log.debug("Emitting DTO for updated event: " + updated);
              // Emit DTO for updated event
              em.emit(ProductRegistryEventDtoMapper.INSTANCE.toDto(updated));
            } else {
              // Fail the stream on unexpected event types
              Throwable error = new ProductRegistryEventStreamException("Unexpected event type: " + evt.getClass().getName());
              em.fail(error);
              return;
            }
            // Acknowledge the message
            consumer.acknowledge(msg.get());
          } catch (PulsarClientException e) {
            Log.error("Failed to receive event from consumer.", e);
            em.fail(e);
            return;
          }
        }
      });
    });
  }

  /**
   * Endpoint to remove a product.
   * 
   * @param removeProduct - DTO containing the product details
   * @param uriInfo       - URI info for building the response URI
   * @return Response indicating the product removal was accepted
   */
  @POST
  @Path("/removeProduct")
  @Consumes("application/json")
  public Response removeProduct(RemoveProductCommandDto removeProduct, @Context UriInfo uriInfo) {
    final RemoveProduct removeProductCommand = ProductRegistryCommandDtoMapper.INSTANCE.toCommand(removeProduct);
    final String correlationId = UUID.randomUUID().toString();
    commandEmitter.send(
        PulsarOutgoingMessage.from(Message.of(removeProductCommand))
            .addMetadata(PulsarOutgoingMessageMetadata.builder()
                .withProperties(Map.of("correlation-id", correlationId))
                .build()));
    return Response
        .seeOther(
            uriInfo.getBaseUriBuilder()
                .path(this.getClass())
                .path("/events/removed?correlationId=" + correlationId)
                .build())
        .build();
  }

  @GET
  @Path("/events/productRemoved")
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<ProductRemovedEventDto> removedEventStream(@QueryParam("correlationId") String correlationId) {
    // Create a stream of product registry events
    return Multi.createFrom().emitter(em -> {
      // Create consumer for product registry events with the given correlation id
      final Consumer<ProductRegistryEvent> consumer = getEventsConsumerByCorrelationId(correlationId);
      // Close the consumer on termination
      em.onTermination(() -> {
        try {
          consumer.unsubscribe();
        } catch (PulsarClientException e) {
          Log.error("Failed to close consumer for product registry events.", e);
        }
      });
      // Consume events and emit DTOs
      CompletableFuture.runAsync(() -> {
        while(!em.isCancelled()) {
          try {
            final var timeout = 10000;
            final var msg = Optional.ofNullable(consumer.receive(timeout, TimeUnit.MILLISECONDS));
            if (msg.isEmpty()) {
              // Complete the emitter if no event is received within the timeout. Free up resources.
              Log.debug("No event received within timeout of " + timeout + " seconds.");
              em.complete();
            }
            final ProductRegistryEvent evt = msg.get().getValue();
            Log.debug("Received event: " + evt);
            // Map event to DTO
            if (evt instanceof ProductRemoved removed) {
              Log.debug("Emitting DTO for removed event: " + removed);
              // Emit DTO for removed event
              em.emit(ProductRegistryEventDtoMapper.INSTANCE.toDto(removed));
            } else {
              // Fail the stream on unexpected event types
              Throwable error = new ProductRegistryEventStreamException("Unexpected event type: " + evt.getClass().getName());
              em.fail(error);
              return;
            }
            // Acknowledge the message
            consumer.acknowledge(msg.get());
          } catch (PulsarClientException e) {
            Log.error("Failed to receive event from consumer.", e);
            em.fail(e);
            return;
          }
        }
      });
    });
  }

  /**
   * Create a consumer for product registry events with the given correlation id.
   * 
   * Useful for consuming events with a specific correlation id to avoid consuming
   * events from other
   * producers.
   * 
   * @param correlationId - correlation id to use for the consumer
   * @return Consumer for product registry events
   */
  private Consumer<ProductRegistryEvent> getEventsConsumerByCorrelationId(String correlationId) {
    try {
      // Define the channel name, topic and schema for the consumer
      final String channelName = ProductRegistryEventChannelName.PRODUCT_REGISTRY_EVENT.toString();
      final String topic = channelName + "-" + correlationId;
      // Create and return the subscription (consumer)
      return pulsarClients.getClient(channelName)
          .newConsumer(Schema.JSON(ProductRegistryEvent.class))
          .subscriptionName(topic)
          .topic(topic)
          .subscribe();
    } catch (PulsarClientException e) {
      throw new PublicRegistryEventStreamException("Failed to create consumer for product registry events.", e);
    }
  }
}

class PublicRegistryEventStreamException extends RuntimeException {
  public PublicRegistryEventStreamException(String message, Throwable cause) {
    super(message, cause);
  }
}
