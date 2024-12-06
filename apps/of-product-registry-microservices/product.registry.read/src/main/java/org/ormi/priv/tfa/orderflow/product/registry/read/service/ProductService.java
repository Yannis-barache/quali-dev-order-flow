package org.ormi.priv.tfa.orderflow.product.registry.read.service;

import java.util.List;
import java.util.Optional;

import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;
import org.ormi.priv.tfa.orderflow.product.registry.read.projection.repository.ProductRepository;
import org.ormi.priv.tfa.orderflow.product.registry.read.projection.repository.model.ProductEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service to get product informations.
 * 
 * Access the product informations from the database through the repository.
 */
@ApplicationScoped
public class ProductService {

  /**
   * The product repository.
   */
  @Inject
  private ProductRepository productRepository;

  /**
   * Persist a new product.
   * 
   * @param product the product to create
   */
  public void createProduct(ProductEntity product) {
    productRepository.persist(product);
  }

  public void updateProduct(ProductEntity product) {
    productRepository.update(product);
  }

  /**
   * Get a product by its id.
   * 
   * @param productId the product id
   * @return the product
   */
  public Optional<ProductEntity> getProductById(ProductId productId) {
    return productRepository.findByProductId(productId);
  }

  /**
   * Get all products.
   * 
   * @return the list of products
   */
  public List<ProductEntity> getAllProducts() {
    return productRepository.listAll();
  }

  /**
   * Remove a product by its id.
   * 
   * @param productId the product id
   */
  public void removeProductById(ProductId productId) {
    productRepository.deleteByProductId(productId);
  }
}
