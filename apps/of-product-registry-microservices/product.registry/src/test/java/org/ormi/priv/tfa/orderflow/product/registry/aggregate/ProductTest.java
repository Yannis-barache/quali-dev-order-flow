package org.ormi.priv.tfa.orderflow.product.registry.aggregate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;

public class ProductTest {

  @Nested
  public class Equals {

    @Test
    public void it_should_returnTrue_when_comparingProductsWithSameId() {
      // Given
      ProductId productId = new ProductId();
      Product product1 = new Product(productId, "Test 1", "A test product");
      Product product2 = new Product(productId, "Test 2", "Another test product");

      // When
      boolean result = product1.equals(product2);

      // Then
      assertTrue(result);
    }

    @Test
    public void it_should_returnFalse_when_comparingProductsWithDifferentId() {
      // Given
      ProductId productId1 = new ProductId();
      ProductId productId2 = new ProductId();
      Product product1 = new Product(productId1, "Test 1", "A test product");
      Product product2 = new Product(productId2, "Test 2", "Another test product");

      // When
      boolean result = product1.equals(product2);

      // Then
      assertFalse(result);
    }
  }

  @Nested
  public class HashCode {

    @Test
    public void it_should_returnSameHashCode_when_comparingProductsWithSameId() {
      // Given
      ProductId productId = new ProductId();
      Product product1 = new Product(productId, "Test 1", "A test product");
      Product product2 = new Product(productId, "Test 2", "Another test product");

      // When
      int hashCode1 = product1.hashCode();
      int hashCode2 = product2.hashCode();

      // Then
      assertTrue(hashCode1 == hashCode2);
    }

    @Test
    public void it_should_returnDifferentHashCode_when_comparingProductsWithDifferentId() {
      // Given
      ProductId productId1 = new ProductId();
      ProductId productId2 = new ProductId();
      Product product1 = new Product(productId1, "Test 1", "A test product");
      Product product2 = new Product(productId2, "Test 2", "Another test product");

      // When
      int hashCode1 = product1.hashCode();
      int hashCode2 = product2.hashCode();

      // Then
      assertFalse(hashCode1 == hashCode2);
    }
  }

  @Nested
  public class ToString {

    @Test
    public void it_should_returnStringRepresentationOfProduct() {
      // Given
      ProductId productId = new ProductId();
      Product product = new Product(productId, "Test 1", "A test product");

      // When
      String result = product.toString();

      // Then
      assertTrue(result.contains("Product{"));
      assertTrue(result.contains("productId=" + productId));
      assertTrue(result.contains("name='Test 1'"));
      assertTrue(result.contains("productDescription='A test product'"));
    }
  }
}
