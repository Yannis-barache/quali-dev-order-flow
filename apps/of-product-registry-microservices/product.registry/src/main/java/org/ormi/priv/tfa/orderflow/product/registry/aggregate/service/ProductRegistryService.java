package org.ormi.priv.tfa.orderflow.product.registry.aggregate.service;

import java.time.Instant;

import org.ormi.priv.tfa.orderflow.lib.event.sourcing.aggregate.EventId;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.RegisterProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.RemoveProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.UpdateProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistered;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistryEvent;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRemoved;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductUpdated;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.ProductRegistry;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.ProductRegistryEventRepository;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.mapper.ProductRegistryEventEntityMapper;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model.ProductRegisteredEventEntity;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model.ProductRemovedEventEntity;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.repository.model.ProductUpdatedEventEntity;
import org.ormi.priv.tfa.orderflow.product.registry.service.producer.ProductRegistryEventEmitter;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Product registry service to handle commands and interact with the persistance
 */
@ApplicationScoped
public class ProductRegistryService {
  private static final String PRODUCT_REGISTRY_ID = "GLOBAL_REGISTRY";

  /**
   * Product registry repository to interact with the persistance layer.
   */
  @Inject
  private ProductRegistryEventRepository productRegistryRepository;

  /**
   * Product registry projector to project events to the read model.
   */
  @Inject
  private ProductRegistryEventEmitter eventEmitter;

  /**
   * Load the product registry from the event store.
   * 
   * This method will load all events from the event store and apply them to the
   * product registry starting from the last known version.
   * 
   * @return The product registry.
   */
  public ProductRegistry loadProductRegistry(ProductRegistry registry) {
    productRegistryRepository.findEventsByAggregateRootIdAndStartingVersion(PRODUCT_REGISTRY_ID, registry.getVersion())
        .stream()
        .<ProductRegistryEvent>map(evtEntity -> {
          if (evtEntity instanceof ProductRegisteredEventEntity) { // if the event represents an added product
            return (ProductRegistryEvent) ProductRegistryEventEntityMapper.INSTANCE
                .toEvent((ProductRegisteredEventEntity) evtEntity);
          } else if (evtEntity instanceof ProductUpdatedEventEntity) {  // if the event represents an updated product
            return (ProductRegistryEvent) ProductRegistryEventEntityMapper.INSTANCE
                .toEvent((ProductUpdatedEventEntity) evtEntity);
          } else if (evtEntity instanceof ProductRemovedEventEntity) { // if the event represents a removed product
            return (ProductRegistryEvent) ProductRegistryEventEntityMapper.INSTANCE
                .toEvent((ProductRemovedEventEntity) evtEntity);
          }
          throw new IllegalArgumentException("Unknown event type: " + evtEntity.getClass());
        })
        .forEach(registry::apply);
    return registry;
  }

  /**
   * Handle the register command, save and return an event.
   * 
   * @param registerProduct - The register product command.
   * @return The product registered event.
   */
  @Transactional(Transactional.TxType.MANDATORY)
  public Uni<ProductRegistered> registerProduct(ProductRegistry registry, RegisterProduct registerProduct) {
    Log.debug("Registering product: " + registerProduct);
    // Check if the product name is available
    // TODO: Inverse the check to simplify the code
    if (registry.isProductNameAvailable(registerProduct.getName())) {
      // Create and save the event
      final ProductRegistered evt = new ProductRegistered(
          new EventId(),
          PRODUCT_REGISTRY_ID,
          registry.incrementVersion(),
          Instant.now().toEpochMilli(),
          new ProductRegistered.Payload(
              new ProductId(),
              registerProduct.getName(),
              registerProduct.getProductDescription()));
      productRegistryRepository.saveEvent(ProductRegistryEventEntityMapper.INSTANCE.toEntity(evt));

      // Emit the event
      eventEmitter.emit(evt);

      return Uni.createFrom().item(evt);
    }
    // Product name is not available
    return Uni.createFrom()
        .failure(new IllegalArgumentException("Product name already in use: " + registerProduct.getName()));
  }

  /**
   * Handle the update command, save and return an event.
   * 
   * @param updateProduct - The update product command.
   * @return The product updated event.
   */
  @Transactional(Transactional.TxType.MANDATORY)
  public Uni<ProductUpdated> updateProduct(ProductRegistry registry, UpdateProduct updateProduct) {
    Log.debug("Updating product: " + updateProduct);
    // Check if the product exists
    if (!registry.hasProductWithId(updateProduct.getProductId())) {
      return Uni.createFrom()
          .failure(new IllegalArgumentException("Product not found: " + updateProduct.getProductId()));
    }
    if (!registry.isProductNameAvailable(updateProduct.getName())) {
      return Uni.createFrom()
          .failure(new IllegalArgumentException("Product name already in use: " + updateProduct.getName()));
    }
    // Create and save the event
    final ProductUpdated evt = new ProductUpdated(
        new EventId(),
        PRODUCT_REGISTRY_ID,
        registry.incrementVersion(),
        Instant.now().toEpochMilli(),
        new ProductUpdated.Payload(
            updateProduct.getProductId(),
            updateProduct.getName(),
            updateProduct.getProductDescription()));
    productRegistryRepository.saveEvent(ProductRegistryEventEntityMapper.INSTANCE.toEntity(evt));

    // Emit the event
    eventEmitter.emit(evt);

    return Uni.createFrom().item(evt);
  }

  /**
   * Handle the remove command, save and return an event.
   * 
   * @param removeProduct - The remove product command.
   * @return The product removed event.
   */
  @Transactional(Transactional.TxType.MANDATORY)
  public Uni<ProductRemoved> removeProduct(ProductRegistry registry, RemoveProduct removeProduct) {
    Log.debug("Removing product: " + removeProduct);
    // Check if the product exists
    if (!registry.hasProductWithId(removeProduct.getProductId())) {
      return Uni.createFrom()
          .failure(new IllegalArgumentException("Product not found: " + removeProduct.getProductId()));
    }
    // Create and save the event
    final ProductRemoved evt = new ProductRemoved(
        new EventId(),
        PRODUCT_REGISTRY_ID,
        registry.incrementVersion(),
        Instant.now().toEpochMilli(),
          new ProductRemoved.Payload(removeProduct.getProductId()));
    productRegistryRepository.saveEvent(ProductRegistryEventEntityMapper.INSTANCE.toEntity(evt));

    // Emit the event
    eventEmitter.emit(evt);

    return Uni.createFrom().item(evt);
  }
}
