package org.ormi.priv.tfa.orderflow.product.registry.read.projection;

import java.util.Optional;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistered;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistryEvent;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRemoved;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductUpdated;
import org.ormi.priv.tfa.orderflow.product.registry.read.projection.repository.model.ProductEntity;
import org.ormi.priv.tfa.orderflow.product.registry.read.service.ProductService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Product registry projector to project events to the read model.
 * 
 * @implNote This class is responsible for projecting the events to the read
 *           model.
 *           It reacts to the events and updates the read model accordingly.
 *           As the projector is in downstream of the event emitter, it is not
 *           his responsibility to verify integrity of the states
 *           as the aggregate and event emitter should have already done that.
 */
@ApplicationScoped
public class ProductRegistryProjector {

  /**
   * The product service.
   */
  @Inject
  private ProductService productService;

  /**
   * Handle the event.
   * 
   * @param event - the event to handle
   */
  public void handleEvent(ProductRegistryEvent event) {
    if (event instanceof ProductRegistered) {
      projectRegisteredProduct((ProductRegistered) event);
    } else if (event instanceof ProductUpdated) {
      projectUpdatedProduct((ProductUpdated) event);
    } else if (event instanceof ProductRemoved) {
      projectRemovedProduct((ProductRemoved) event);
    }
  }

  /**
   * Project a registered product.
   * 
   * @param registered - the event to project
   */
  public void projectRegisteredProduct(ProductRegistered registered) {
    // Create a new product entity
    final ProductEntity product = new ProductEntity();
    product.productId = registered.payload.productId.getId();
    product.name = registered.payload.name;
    product.description = registered.payload.productDescription;
    
    // Persist the product
    productService.createProduct(product);
  }

  /**
   * Project an updated product.
   * 
   * @param updated - the event to project
   */
  public void projectUpdatedProduct(ProductUpdated updated) {
    // Get the product entity
    final Optional<ProductEntity> result = productService.getProductById(updated.payload.productId);
    if (result.isEmpty()) {
      // The product does not exist
      // TODO: Log an error
      return;
    }
    // Update the product
    final ProductEntity product = result.get();
    product.name = updated.payload.name;
    product.description = updated.payload.productDescription;
    // Persist the product
    productService.updateProduct(product);
  }

  /**
   * Project a removed product.
   * 
   * @param removed - the event to project
   */
  public void projectRemovedProduct(ProductRemoved removed) {
    // Remove the product
    productService.removeProductById(removed.payload.productId);
  }
}
