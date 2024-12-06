package org.ormi.priv.tfa.orderflow.product.registry.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command.*;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event.*;
import org.ormi.priv.tfa.orderflow.lib.publishedlanguage.valueobject.ProductId;
import org.ormi.priv.tfa.orderflow.product.registry.aggregate.service.ProductRegistryService;

import io.smallrye.mutiny.Uni;

public class ProductRegistryTest {

  private ProductRegistryService productRegistryService;
  private ProductRegistry productRegistry;

  @BeforeEach
  public void setUp() {
    productRegistryService = Mockito.mock(ProductRegistryService.class);
    productRegistry = new ProductRegistry(productRegistryService);
  }

  @Nested
  class Handle {

    @Test
    public void it_should_registerProduct_when_validCommand() {
      // Given
      ProductId productId = new ProductId();
      RegisterProduct command = new RegisterProduct("Product 1", "Description");
      ProductRegistered event = new ProductRegistered(productId, "1","Product 1", "2024-11-20","Description");

      Mockito.when(productRegistryService.registerProduct(Mockito.eq(productRegistry), Mockito.any()))
          .thenReturn(Uni.createFrom().item(event));

      // When
      Uni<? extends ProductRegistryEvent> result = productRegistry.handle(command);

      // Then
      assertDoesNotThrow(() -> {
        ProductRegistryEvent actualEvent = result.await().indefinitely();
        assertTrue(actualEvent instanceof ProductRegistered);
        assertTrue(productRegistry.hasProductWithId(productId));
      });
    }

    @Test
    public void it_should_fail_when_nullCommand() {
      // Given
      ProductRegistryCommand command = null;

      // When & Then
      assertThrows(IllegalArgumentException.class, () -> productRegistry.handle(command).await().indefinitely());
    }

    @Test
    public void it_should_updateProduct_when_validCommand() {
      // Given
      ProductId productId = new ProductId();
      UpdateProduct command = new UpdateProduct(productId, "Updated Product", "Updated Description");
      ProductUpdated event = new ProductUpdated(productId, "Updated Product", "Updated Description");

      Mockito.when(productRegistryService.updateProduct(Mockito.eq(productRegistry), Mockito.any()))
          .thenReturn(Uni.createFrom().item(event));

      // When
      Uni<? extends ProductRegistryEvent> result = productRegistry.handle(command);

      // Then
      assertDoesNotThrow(() -> {
        ProductRegistryEvent actualEvent = result.await().indefinitely();
        assertTrue(actualEvent instanceof ProductUpdated);
      });
    }

    @Test
    public void it_should_removeProduct_when_validCommand() {
      // Given
      ProductId productId = new ProductId();
      RemoveProduct command = new RemoveProduct(productId);
      ProductRemoved event = new ProductRemoved(productId);

      Mockito.when(productRegistryService.removeProduct(Mockito.eq(productRegistry), Mockito.any()))
          .thenReturn(Uni.createFrom().item(event));

      // When
      Uni<? extends ProductRegistryEvent> result = productRegistry.handle(command);

      // Then
      assertDoesNotThrow(() -> {
        ProductRegistryEvent actualEvent = result.await().indefinitely();
        assertTrue(actualEvent instanceof ProductRemoved);
        assertFalse(productRegistry.hasProductWithId(productId));
      });
    }
  }

  @Nested
  class Apply {

    @Test
    public void it_should_registerProduct_when_validEvent() {
      // Given
      ProductId productId = new ProductId();
      ProductRegistered event = new ProductRegistered(productId, "Product 1", "Description");

      // When
      productRegistry.apply(event);

      // Then
      assertTrue(productRegistry.hasProductWithId(productId));
    }

    @Test
    public void it_should_updateProduct_when_validEvent() {
      // Given
      ProductId productId = new ProductId();
      ProductRegistered registerEvent = new ProductRegistered(productId, "Product 1", "Description");
      ProductUpdated updateEvent = new ProductUpdated(productId, "Updated Product", "Updated Description");

      // When
      productRegistry.apply(registerEvent);
      productRegistry.apply(updateEvent);

      // Then
      assertTrue(productRegistry.hasProductWithId(productId));
      assertTrue(productRegistry.isProductNameAvailable("Product 1"));
    }

    @Test
    public void it_should_removeProduct_when_validEvent() {
      // Given
      ProductId productId = new ProductId();
      ProductRegistered registerEvent = new ProductRegistered(productId, "Product 1", "Description");
      ProductRemoved removeEvent = new ProductRemoved(productId);

      // When
      productRegistry.apply(registerEvent);
      productRegistry.apply(removeEvent);

      // Then
      assertFalse(productRegistry.hasProductWithId(productId));
    }

    @Test
    public void it_should_fail_when_nullEvent() {
      // Given
      ProductRegistryEvent event = null;

      // When & Then
      assertThrows(NullPointerException.class, () -> productRegistry.apply(event));
    }
  }

  @Nested
  class GetVersion {

    @Test
    public void it_should_returnVersion() {
      // Given
      long version = productRegistry.getVersion();

      // Then
      assertEquals(0, version);
    }

    @Test
    public void it_should_incrementVersion_afterUpdate() {
      // When
      productRegistry.incrementVersion();

      // Then
      assertEquals(1, productRegistry.getVersion());
    }
  }

  @Nested
  class HasProductWithId {

    @Test
    public void it_should_returnTrue_when_productExists() {
      // Given
      ProductId productId = new ProductId();
      ProductRegistered event = new ProductRegistered(productId, "Product 1", "Description");
      productRegistry.apply(event);

      // When
      boolean result = productRegistry.hasProductWithId(productId);

      // Then
      assertTrue(result);
    }

    @Test
    public void it_should_returnFalse_when_productDoesNotExist() {
      // Given
      ProductId productId = new ProductId();

      // When
      boolean result = productRegistry.hasProductWithId(productId);

      // Then
      assertFalse(result);
    }
  }

  @Nested
  class IsProductNameAvailable {

    @Test
    public void it_should_returnTrue_when_nameAvailable() {
      // When
      boolean result = productRegistry.isProductNameAvailable("Available Name");

      // Then
      assertTrue(result);
    }

    @Test
    public void it_should_returnFalse_when_nameNotAvailable() {
      // Given
      ProductId productId = new ProductId();
      ProductRegistered event = new ProductRegistered(productId, "Existing Name", "Description");
      productRegistry.apply(event);

      // When
      boolean result = productRegistry.isProductNameAvailable("Existing Name");

      // Then
      assertFalse(result);
    }
  }
}