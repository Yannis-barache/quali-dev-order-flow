package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ProductIdTest {

  @Nested
  public class Equals {

    @Test
    public void it_should_returnTrue_when_comparingIdsOfTheSameValue() {
      // Given
      String id = UUID.randomUUID().toString();
      ProductId productId = ProductId.of(id);
      ProductId otherProductId = ProductId.of(id);

      // When
      boolean result = productId.equals(otherProductId);

      // Then
      assertTrue(result);
    }

    @Test
    public void it_should_returnFalse_when_comparingIdsOfDifferentValues() {
      // Given
      ProductId productId = new ProductId();
      ProductId otherProductId = new ProductId();

      // When
      boolean result = productId.equals(otherProductId);

      // Then
      assertTrue(!result);
    }
  }

  @Nested
  public class HashCode {

    @Test
    public void it_should_returnSameHashCode_when_comparingIdsOfTheSameValue() {
      // Given
      String id = UUID.randomUUID().toString();
      ProductId productId = ProductId.of(id);
      ProductId otherProductId = ProductId.of(id);

      // When
      int hashCode1 = productId.hashCode();
      int hashCode2 = otherProductId.hashCode();

      // Then
      assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void it_should_returnDifferentHashCode_when_comparingIdsOfDifferentValues() {
      // Given
      ProductId productId = new ProductId();
      ProductId otherProductId = new ProductId();

      // When
      int hashCode1 = productId.hashCode();
      int hashCode2 = otherProductId.hashCode();

      // Then
      assertNotEquals(hashCode1, hashCode2);
    }
  }

  @Nested
  public class ToString {

    @Test
    public void it_should_returnStringRepresentationOfProductId() {
      // Given
      String id = UUID.randomUUID().toString();
      ProductId productId = ProductId.of(id);

      // When
      String result = productId.toString();

      // Then
      assertEquals(String.format("ProductId{value='%s'}", id), result);
    }
  }
}
