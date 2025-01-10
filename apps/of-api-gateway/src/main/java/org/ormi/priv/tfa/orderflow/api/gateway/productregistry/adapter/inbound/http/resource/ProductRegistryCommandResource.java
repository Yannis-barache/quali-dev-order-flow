package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.resteasy.reactive.RestStreamElementType;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.*;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.mapper.ProductRegistryCommandDtoMapper;
import org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.dto.mapper.ProductRegistryEventDtoMapper;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.*;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.*;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.config.ProductRegistryEventChannelName;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.pulsar.PulsarClientService;
import io.smallrye.reactive.messaging.pulsar.PulsarOutgoingMessage;
import io.smallrye.reactive.messaging.pulsar.PulsarOutgoingMessageMetadata;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/product/registry")
public class ProductRegistryCommandResource {

  @ConfigProperty(name = "product.registry.stream.timeout")
  int timeout;

  @Inject
  PulsarClientService pulsarClients;

  @Inject
  @Channel("product-registry-command")
  Emitter<ProductRegistryCommand> commandEmitter;

  private final EventStreamManager eventStreamManager;

  @Inject
  public ProductRegistryCommandResource(PulsarClientService pulsarClients) {
    this.eventStreamManager = new EventStreamManager(pulsarClients, timeout);
  }

  @POST
  @Path("/registerProduct")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registerProduct(RegisterProductCommandDto cmdDto, @Context UriInfo uriInfo) {
    final RegisterProduct command = ProductRegistryCommandDtoMapper.INSTANCE.toCommand(cmdDto);
    final String correlationId = sendCommand(command);
    return buildEventResponse(uriInfo, "productRegistered", correlationId);
  }

  @POST
  @Path("/updateProduct")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateProduct(UpdateProductCommandDto cmdDto, @Context UriInfo uriInfo) {
    final UpdateProduct command = ProductRegistryCommandDtoMapper.INSTANCE.toCommand(cmdDto);
    final String correlationId = sendCommand(command);
    return buildEventResponse(uriInfo, "productUpdated", correlationId);
  }

  @POST
  @Path("/removeProduct")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response removeProduct(RemoveProductCommandDto cmdDto, @Context UriInfo uriInfo) {
    final RemoveProduct command = ProductRegistryCommandDtoMapper.INSTANCE.toCommand(cmdDto);
    final String correlationId = sendCommand(command);
    return buildEventResponse(uriInfo, "productRemoved", correlationId);
  }

  @GET
  @Path("/events/productRegistered")
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<ProductRegisteredEventDto> registeredEventStream(@QueryParam("correlationId") String correlationId) {
    return eventStreamManager.createEventStream(correlationId, ProductRegistered.class,
        ProductRegistryEventDtoMapper.INSTANCE::toDto);
  }

  @GET
  @Path("/events/productUpdated")
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<ProductUpdatedEventDto> updatedEventStream(@QueryParam("correlationId") String correlationId) {
    return eventStreamManager.createEventStream(correlationId, ProductUpdated.class,
        ProductRegistryEventDtoMapper.INSTANCE::toDto);
  }

  @GET
  @Path("/events/productRemoved")
  @RestStreamElementType(MediaType.APPLICATION_JSON)
  public Multi<ProductRemovedEventDto> removedEventStream(@QueryParam("correlationId") String correlationId) {
    return eventStreamManager.createEventStream(correlationId, ProductRemoved.class,
        ProductRegistryEventDtoMapper.INSTANCE::toDto);
  }

  private String sendCommand(ProductRegistryCommand command) {
    final String correlationId = UUID.randomUUID().toString();
    commandEmitter.send(
        PulsarOutgoingMessage.from(Message.of(command))
            .addMetadata(PulsarOutgoingMessageMetadata.builder()
                .withProperties(Map.of("correlation-id", correlationId))
                .build()));
    return correlationId;
  }

  private Response buildEventResponse(UriInfo uriInfo, String eventPath, String correlationId) {
    return Response
        .seeOther(
            uriInfo.getBaseUriBuilder()
                .path(this.getClass())
                .path("/events/" + eventPath)
                .queryParam("correlationId", correlationId)
                .build())
        .build();
  }
}

class EventStreamManager {
  private final PulsarClientService pulsarClients;
  private final int timeout;

  public EventStreamManager(PulsarClientService pulsarClients, int timeout) {
    this.pulsarClients = pulsarClients;
    this.timeout = timeout;
  }

  public <E extends ProductRegistryEvent, D> Multi<D> createEventStream(String correlationId, Class<E> eventType, java.util.function.Function<E, D> mapper) {
    return Multi.createFrom().emitter(em -> {
      final Consumer<ProductRegistryEvent> consumer = createConsumer(correlationId);
      em.onTermination(() -> closeConsumer(consumer));

      CompletableFuture.runAsync(() -> {
        while (!em.isCancelled()) {
          try {
            var msg = Optional.ofNullable(consumer.receive(timeout, TimeUnit.MILLISECONDS));
            if (msg.isEmpty()) {
              Log.debug("No event received within timeout of " + timeout + " ms.");
              em.complete();
            } else {
              var event = msg.get().getValue();
              if (eventType.isInstance(event)) {
                em.emit(mapper.apply(eventType.cast(event)));
              } else {
                em.fail(new ProductRegistryEventStreamException("Unexpected event type: " + event.getClass().getName()));
              }
              consumer.acknowledge(msg.get());
            }
          } catch (PulsarClientException e) {
            Log.error("Error while receiving event.", e);
            em.fail(e);
          }
        }
      });
    });
  }

  private Consumer<ProductRegistryEvent> createConsumer(String correlationId) {
    try {
      final String channelName = ProductRegistryEventChannelName.PRODUCT_REGISTRY_EVENT.toString();
      final String topic = channelName + "-" + correlationId;
      return pulsarClients.getClient(channelName)
          .newConsumer(Schema.JSON(ProductRegistryEvent.class))
          .subscriptionName(topic)
          .topic(topic)
          .subscribe();
    } catch (PulsarClientException e) {
      throw new ProductRegistryEventStreamException("Failed to create consumer for events.", e);
    }
  }

  private void closeConsumer(Consumer<?> consumer) {
    try {
      consumer.unsubscribe();
    } catch (PulsarClientException e) {
      Log.error("Failed to close consumer.", e);
    }
  }
}

class ProductRegistryEventStreamException extends RuntimeException {
  public ProductRegistryEventStreamException(String message) {
    super(message);
  }

  public ProductRegistryEventStreamException(String message, Throwable cause) {
    super(message, cause);
  }
}