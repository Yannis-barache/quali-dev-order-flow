package org.ormi.priv.tfa.orderflow.product.registry.read.service;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistryEvent;
import org.ormi.priv.tfa.orderflow.product.registry.read.projection.ProductRegistryProjector;
import org.ormi.priv.tfa.orderflow.product.registry.read.emitter.ProductRegistryEventEmitter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProductRegistryEventConsumer {

  @Inject
  private ProductRegistryProjector projector;

  @Inject
  private ProductRegistryEventEmitter eventEmitter;

  @Incoming("product-registry-event")
  @Transactional(Transactional.TxType.REQUIRED)
  public void handleEvent(ProductRegistryEvent event) {
    // Project the event
    projector.handleEvent(event);
    
    // Sink the event
    eventEmitter.sink(event.getCorrelationId(), event);
  }
}