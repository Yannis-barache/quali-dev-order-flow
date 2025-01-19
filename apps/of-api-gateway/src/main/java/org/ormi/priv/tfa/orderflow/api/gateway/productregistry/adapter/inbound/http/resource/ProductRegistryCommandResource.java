package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import io.smallrye.mutiny.subscription.MultiEmitter;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.resteasy.reactive.RestStreamElementType;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductRegisteredEventDto;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.ProductRegistryEventDto;
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
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistryMessage;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRemoved;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductUpdated;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.config.ProductRegistryEventChannelName;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistryError;

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
   * Timeout of the events
   */
  @ConfigProperty(name = "product.registry.stream.timeout")
  int timeout;

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
    return Multi.createFrom().emitter(em -> {

      // Create consumer for product registry events with the given correlation id
      final Consumer<ProductRegistryMessage> consumer = getEventsConsumerByCorrelationId(correlationId);
      // Close the consumer on termination
      em.onTermination(() -> {
        try {
          consumer.unsubscribe();
        } catch (PulsarClientException e) {
          Log.error("Failed to close consumer for product registry events.", e);
        }
      });

      CompletableFuture.runAsync(() -> {

        while (!em.isCancelled()) {
          processEvent(
              consumer,
              timeout,
              em,
              evt -> {
                if (evt instanceof ProductRegistered registered) {
                  return ProductRegistryEventDtoMapper.INSTANCE.toDto(registered);
                }
                return null; // Événement inattendu
              }
          );
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
    return Multi.createFrom().emitter(em -> {
      // Create consumer for product registry events with the given correlation id
      final Consumer<ProductRegistryMessage> consumer = getEventsConsumerByCorrelationId(correlationId);
      // Close the consumer on termination
      em.onTermination(() -> {
        try {
          consumer.unsubscribe();
        } catch (PulsarClientException e) {
          Log.error("Failed to close consumer for product registry events.", e);
        }
      });

      CompletableFuture.runAsync(() -> {
        while (!em.isCancelled()) {
          processEvent(
              consumer,
              timeout,
              em,
              evt -> {
                if (evt instanceof ProductUpdated updated) {
                  return ProductRegistryEventDtoMapper.INSTANCE.toDto(updated);
                }
                return null; // Événement inattendu
              }
          );
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
    return Multi.createFrom().emitter(em -> {

      // Create consumer for product registry events with the given correlation id
      final Consumer<ProductRegistryMessage> consumer = getEventsConsumerByCorrelationId(correlationId);
      // Close the consumer on termination
      em.onTermination(() -> {
        try {
          consumer.unsubscribe();
        } catch (PulsarClientException e) {
          Log.error("Failed to close consumer for product registry events.", e);
        }
      });

      CompletableFuture.runAsync(() -> {
        while (!em.isCancelled()) {
          processEvent(
              consumer,
              timeout,
              em,
              evt -> {
                if (evt instanceof ProductRemoved removed) {
                  return ProductRegistryEventDtoMapper.INSTANCE.toDto(removed);
                }
                return null; // Événement inattendu
              }
          );
        }
      });
    });
  }
  /**
   * Create a consumer for product registry events with the given correlation id.
   * Useful for consuming events with a specific correlation id to avoid consuming
   * events from other
   * producers.
   *
   * @param correlationId - correlation id to use for the consumer
   * @return Consumer for product registry events
   */
  private Consumer<ProductRegistryMessage> getEventsConsumerByCorrelationId(String correlationId) {
    try {
      // Define the channel name, topic and schema for the consumer
      final String channelName = ProductRegistryEventChannelName.PRODUCT_REGISTRY_EVENT_MESSAGE.toString();
      final String topic = channelName + "-" + correlationId;
      // Create and return the subscription (consumer)
      return pulsarClients.getClient(channelName)
          .newConsumer(Schema.JSON(ProductRegistryMessage.class))
          .subscriptionName(topic)
          .topic(topic)
          .subscribe();
    } catch (PulsarClientException e) {
      throw new PublicRegistryEventStreamException("Failed to create consumer for product registry events.", e);
    }
  }

  public static <T extends ProductRegistryEventDto> void processEvent(
      Consumer<ProductRegistryMessage> consumer,
      long timeout,
      MultiEmitter<? super T> em,
      java.util.function.Function<ProductRegistryEvent, T> eventMapper
  ) {
    try {
      // Recevoir un message du consommateur avec un délai d'attente
      final var msg = Optional.ofNullable(consumer.receive((int) timeout, TimeUnit.MILLISECONDS));

      if (msg.isEmpty()) {
        // Aucun événement reçu, compléter l'émetteur
        Log.debug("No event received within timeout of " + timeout + " seconds.");
        em.complete();
        return;
      }
      
      final ProductRegistryMessage registryMsg = msg.get().getValue();
            Log.debug("Received event: " + registryMsg);

            if (registryMsg instanceof ProductRegistryError){
              Throwable error = new ProductRegistryError();
              em.fail(error);
              return;
            }
      

      // Mapper l'événement à un DTO
      T dto = eventMapper.apply(registryMessage);
      if (dto != null) {
        Log.debug("Emitting DTO: " + dto);
        em.emit(dto);
      } else {
        // Type d'événement inattendu, échec de la diffusion
        Throwable error = new ProductRegistryEventStreamException(
            "Unexpected event type: " + registryMessage.getClass().getName()
        );
        em.fail(error);
        return;
      }

      // Accuser réception du message
      consumer.acknowledge(msg.get());
    } catch (PulsarClientException e) {
      Log.error("Failed to receive event from consumer.", e);
      em.fail(e);
    }
  }
}

class PublicRegistryEventStreamException extends RuntimeException {
  public PublicRegistryEventStreamException(String message, Throwable cause) {
    super(message, cause);
  }
}