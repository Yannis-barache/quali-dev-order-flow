package org.ormi.priv.tfa.orderflow.product.registry.aggregate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.ProductRegistryCommand;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.RegisterProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.RemoveProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.UpdateProduct;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistered;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRegistryEvent;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductRemoved;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.ProductUpdated;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.service.ProductRegistryService;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

/**
 * ProductRegistry aggregate root
 */
public class ProductRegistry {

  /**
   * Product registry service to interact with the registry and persistance layer.
   */
  private ProductRegistryService productRegistryService;

  /**
   * List of products in the registry.
   */
  private ConcurrentHashMap<ProductId, Product> products;

  /**
   * Current version of the registry.
   */
  private AtomicLong currentVersion = new AtomicLong(0);

  /**
   * Default constructor.
   */
  public ProductRegistry(ProductRegistryService productRegistryService) {
    this.productRegistryService = productRegistryService;
    products = new ConcurrentHashMap<>();
  }

  /**
   * Handle product registration.
   * 
   * @param cmd the command to handle
   */
  @Transactional(value = TxType.REQUIRED)
  public Uni<? extends ProductRegistryEvent> handle(ProductRegistryCommand cmd) {
    Log.debug("Handling command: " + cmd.getClass().getName());
    if (cmd instanceof RegisterProduct register) {
      Log.debug("Command: " + register.toString());
      return productRegistryService
          .registerProduct(this, register)
          .onItem().invoke(this::apply)
          .onFailure().invoke(e -> Log.error("Failed to register product", e));
    } else if (cmd instanceof RemoveProduct remove) {
      Log.debug("Command: " + remove.toString());
      return productRegistryService
          .removeProduct(this, remove)
          .onItem().invoke(this::apply)
          .onFailure().invoke(e -> Log.error("Failed to remove product", e));
    } else if (cmd instanceof UpdateProduct update) {
      Log.debug("Command: " + update.toString());
      return productRegistryService
          .updateProduct(this, update)
          .onItem().invoke(this::apply)
          .onFailure().invoke(e -> Log.error("Failed to update product", e));
    } else {
      Log.warn("Unhandled command type: " + cmd.getClass().getName());
      Log.debug("Command: " + cmd.toString());
      return Uni.createFrom().failure(new IllegalArgumentException("Unhandled command type"));
    }
  }

  /**
   * Apply an event.
   * 
   * Thread safe. Should not persist any state changes.
   * 
   * @param event the event to apply
   */
  public void apply(ProductRegistryEvent event) {
    Log.debug("Applying event: " + event.getClass().getName());
    if (event instanceof ProductRegistered registered) {
      final ProductId productId = registered.payload.productId;
      products.put(productId, new Product(
          productId,
          registered.payload.name,
          registered.payload.productDescription));
    } else if (event instanceof ProductRemoved removed) {
      products.remove(removed.payload.productId);
    } else if (event instanceof ProductUpdated updated) {
      final ProductId productId = updated.payload.productId;
      products.compute(productId, (k, v) -> {
        return new Product(
            productId,
            updated.payload.name,
            updated.payload.productDescription);
      });
    } else {
      Log.warn("Unhandled event type: " + event.getClass().getName());
    }
  }

  /**
   * Increment the version of the registry.
   * 
   * @return the new version
   */
  public long incrementVersion() {
    return currentVersion.incrementAndGet();
  }

  /**
   * Get the version of the registry.
   * 
   * @return the version
   */
  public long getVersion() {
    return currentVersion.get();
  }

  /**
   * Check if the registry has a product with the given id.
   * 
   * @param productId - The id to check for
   * @return true if the registry has a product with the given id, false otherwise
   */
  public boolean hasProductWithId(ProductId productId) {
    return products.containsKey(productId);
  }

  /**
   * Check if the registry has a product.
   * 
   * @param product - The product to check for
   * @return true if the product is in the registry, false otherwise
   */
  public boolean hasProduct(Product product) {
    return products.containsKey(product.getProductId())
        || products.values().stream().anyMatch(p -> p.equals(product));
  }

  /**
   * Check if the registry has a product with the given name.
   * 
   * @param name - The name to check for
   * @return true if no product has the given name, false otherwise
   */
  public boolean isProductNameAvailable(String name) {
    return products.values().stream().noneMatch(p -> p.getName().equals(name));
  }
}
