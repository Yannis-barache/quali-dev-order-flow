package org.ormi.priv.tfa.orderflow.product.registry.read.service;

import java.util.concurrent.CompletionStage;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.ProductRegistryQuery.ProductRegistryQueryResult;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.query.config.ProductRegistryQueryChannelName;

// Import of the ProducerCloseException class from the product.registry service
import org.ormi.priv.tfa.orderflow.product.registry.service.exceptions.ProducerCreationException;
import org.ormi.priv.tfa.orderflow.product.registry.service.exceptions.ProducerCloseException;

import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.pulsar.PulsarClientService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProductQueryResultEmitter {

  @Inject
  private PulsarClientService pulsarClients;

  public void sink(String correlationId, ProductRegistryQueryResult result) {
    // Get the producer for the correlation id
    getResultProducerByCorrelationId(correlationId,
        ProductRegistryQueryResult.class)
        .thenAccept((producer) -> {
          // Sink the result
          producer
              .newMessage()
              .value(result)
              .sendAsync()
              .whenComplete((msgId, ex) -> {
                if (ex != null) {
                  throw new ProducerCreationException("Failed to send message", ex);
                }
                Log.debug(String.format("Sinked result with correlation id{%s}", correlationId));
                try {
                  producer.flush();
                  producer.close();
                } catch (PulsarClientException e) {
                  try {
                    throw new ProducerCloseException("Failed to close producer", e);
                  } catch (ProducerCloseException exc) {
                    throw new RuntimeException(exc);
                  }
                }
              });
        });
  }

  /**
   * Get the producer for the correlation id.
   * 
   * @param <T>           - the type of the result
   * @param correlationId - the correlation id
   * @param resultType    - the type of the result
   * @return the producer
   */
  private <T> CompletionStage<Producer<T>> getResultProducerByCorrelationId(String correlationId, Class<T> resultType) {
    // Define the channel name, topic and schema definition
    final String channelName = ProductRegistryQueryChannelName.PRODUCT_REGISTRY_READ_RESULT.toString();
    final String topic = channelName + "-" + correlationId;
    // Create and return the producer
    return pulsarClients.getClient(channelName)
        .newProducer(Schema.JSON(resultType))
        .producerName(topic)
        .topic(topic)
        .createAsync()
        .exceptionally(
            ex -> {
              throw new RuntimeException("Failed to create producer for correlation id: " + correlationId, ex);
            });
  }
}
