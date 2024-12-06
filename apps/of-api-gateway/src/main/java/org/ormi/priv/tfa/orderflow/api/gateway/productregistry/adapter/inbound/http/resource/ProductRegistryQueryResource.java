package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProductById;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProducts;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.ProductRegistryQuery;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProductById.GetProductByIdResult;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.GetProducts.GetProductsResult;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.config.ProductRegistryQueryChannelName;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.NotFound;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.dto.RegistryProductDto;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.model.dto.RegistryProductDtoCollection;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;

import io.smallrye.reactive.messaging.pulsar.PulsarClientService;
import io.smallrye.reactive.messaging.pulsar.PulsarOutgoingMessage;
import io.smallrye.reactive.messaging.pulsar.PulsarOutgoingMessageMetadata;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * The product registry query resource.
 * 
 * Handles the incoming HTTP requests for the product registry query.
 */
@Path("/productRegistry")
public class ProductRegistryQueryResource {

  /**
   * The Pulsar client service.
   */
  @Inject
  PulsarClientService pulsarClients;

  /**
   * The query emitter. Used to send queries to the product registry.
   */
  @Channel("product-registry-query")
  Emitter<ProductRegistryQuery> queryEmitter;

  /**
   * Get the product by id.
   * 
   * @param productId - the product id
   * @return the response containing the product, or an error message
   */
  @GET
  @Path("/products/{productId}")
  public Response getProduct(@PathParam("productId") String productId) {
    // Create the query to get the product by id
    final GetProductById getProductById = new GetProductById(ProductId.of(productId));
    final String correlationId = java.util.UUID.randomUUID().toString();
    final int timeout = 10000;
    // Send the query to the product registry
    queryEmitter.send(
        PulsarOutgoingMessage.from(Message.of(getProductById))
            .addMetadata(PulsarOutgoingMessageMetadata.builder()
                .withProperties(Map.of("correlation-id", correlationId))
                .build()));
    // Get the consumer for the result
    final Consumer<GetProductByIdResult> consumer = getResultsConsumerByCorrelationId(correlationId,
    GetProductByIdResult.class);
    try {
      // Wait for the result
      final var resultMsg = Optional.ofNullable(consumer.receive(timeout, TimeUnit.MILLISECONDS));
      // Close the consumer
      consumer.close();
      // Check if the result is empty
      if (resultMsg.isEmpty()) {
        return Response
            .status(Status.GATEWAY_TIMEOUT)
            .entity(String.format("Timeout while waiting for product by id{%s}", productId))
            .build();
      }
      // Get the result
      final var result = resultMsg.get().getValue();
      // Check if the result is not found
      if (result instanceof NotFound) {
        return Response
            .status(Status.NOT_FOUND)
            .entity(String.format("Product with id{%s} not found", productId))
            .build();
      }
      // Return the product
      final var productDto = (RegistryProductDto) result;
      return Response.ok(productDto).build();
    } catch (PulsarClientException e) {
      return Response
          .serverError()
          .entity(String.format("Failed to get product by id{%s}", productId))
          .build();
    }
  }

  /**
   * Get all the products.
   * 
   * @return - the response containing the products, or an error message
   */
  @GET
  @Path("/products")
  public Response getAllProducts() {
    // Create the query to get all the products
    final GetProducts getProducts = new GetProducts();
    final String correlationId = java.util.UUID.randomUUID().toString();
    final int timeout = 10000;
    // Send the query to the product registry
    queryEmitter.send(
        PulsarOutgoingMessage.from(Message.of(getProducts))
            .addMetadata(PulsarOutgoingMessageMetadata.builder()
                .withProperties(Map.of("correlation-id", correlationId))
                .build()));
    // Get the consumer for the result
    final Consumer<GetProductsResult> consumer = getResultsConsumerByCorrelationId(correlationId,
    GetProductsResult.class);
    try {
      // Wait for the result
      final var resultMsg = Optional.ofNullable(consumer.receive(timeout, TimeUnit.MILLISECONDS));
      // Close the consumer
      consumer.close();
      // Check if the result is empty
      if (resultMsg.isEmpty()) {
        return Response
            .status(Status.GATEWAY_TIMEOUT)
            .entity("Timeout while waiting for products")
            .build();
      }
      // Get the result
      final var result = resultMsg.get().getValue();
      // Return the products list
      final var productDtos = (RegistryProductDtoCollection) result;
      return Response.ok(productDtos.getProducts()).build();
    } catch (PulsarClientException e) {
      return Response
          .serverError()
          .entity("Failed to get products")
          .build();
    }
  }

  /**
   * Get the results consumer by correlation id.
   * 
   * @param <T> - the type of the result
   * @param correlationId - the correlation id
   * @param resultType - the return type
   * @return the consumer
   */
  private <T> Consumer<T> getResultsConsumerByCorrelationId(String correlationId, Class<T> resultType) {
    try {
      // Define the channel name, topic and schema for the consumer
      final String channelName = ProductRegistryQueryChannelName.PRODUCT_REGISTRY_READ_RESULT.toString();
      final String topic = channelName + "-" + correlationId;
      // Create and return the subscription (consumer)
      return pulsarClients.getClient(channelName)
          .newConsumer(Schema.JSON(resultType))
          .topic(topic)
          .subscriptionName(topic)
          .subscribe();
    } catch (PulsarClientException e) {
      throw new RuntimeException("Failed to create consumer for correlationId: " + correlationId, e);
    }
  }
}
