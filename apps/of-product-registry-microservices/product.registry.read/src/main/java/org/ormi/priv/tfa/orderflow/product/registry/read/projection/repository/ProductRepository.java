package org.ormi.priv.tfa.orderflow.product.registry.read.projection.repository;

import java.util.Optional;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;
import org.ormi.priv.tfa.orderflow.product.registry.read.projection.repository.model.ProductEntity;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for the ProductEntity.
 */
@ApplicationScoped
public class ProductRepository implements PanacheMongoRepository<ProductEntity> {
  
  /**
   * Find a product by its productId.
   * 
   * @param productId - the productId of the product.
   * @return the product.
   */
  public Optional<ProductEntity> findByProductId(ProductId productId) {
    return find("productId", productId.getId()).firstResultOptional();
  }
  
  /**
   * Delete a product by its productId.
   * 
   * @param productId - the productId of the product.
   */
  public void deleteByProductId(ProductId productId) {
    delete("productId", productId.getId());
  }
}
