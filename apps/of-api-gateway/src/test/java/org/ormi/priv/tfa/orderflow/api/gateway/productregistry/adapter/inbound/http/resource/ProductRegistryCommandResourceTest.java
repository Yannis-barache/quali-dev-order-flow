package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource; /**
 * Test class for ProductRegistryCommandResource.
 * This class contains unit tests for the Product Registry API endpoints.
 *
 * The tests are performed using MockMvc to simulate HTTP requests and verify the responses.
 *
 * The following endpoints are tested:
 * - /api/product/registry/registerProduct
 * - /api/product/registry/updateProduct
 * - /api/product/registry/deleteProduct
 *
 * Each endpoint is tested with valid, invalid, and null product data to ensure proper handling of different scenarios.
 *
 * Annotations:
 * - @SpringBootTest: Indicates that the class is a Spring Boot test.
 * - @AutoConfigureMockMvc: Enables and configures MockMvc for the test class.
 *
 * Dependencies:
 * - MockMvc: Used to perform HTTP requests and verify responses.
 * - MediaType: Represents the media type of the request content.
 * - jsonPath: Used to verify JSON response content.
 */
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

package org.ormi.priv.tfa.orderflow.api.gateway.productregistry.adapter.inbound.http.resource;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductRegistryCommandResourceTest {

  @Autowired
  private MockMvc mockMvc;

  /**
   * Test the registerProduct endpoint with valid product data.
   */
  @Test
  public void testRegisterProductWithValidProduct() throws Exception {
    String validProductJson = "{\"name\":\"Valid Product\",\"price\":100.0}";

    mockMvc.perform(post("/api/product/registry/registerProduct")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validProductJson))
        .andExpect(status().is3xxRedirection())
        .andExpect(jsonPath("$.name").value("Valid Product"))
        .andExpect(jsonPath("$.price").value(100.0));
  }


  /**
   * Test the registerProduct endpoint with invalid product data.
   */
  @Test
  public void testRegisterProductWithInvalidProduct() throws Exception {
    String invalidProductJson = "{\"name\":\"\"}";

    mockMvc.perform(post("/api/product/registry/registerProduct")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidProductJson))
        .andExpect(status().isBadRequest());
  }


  /**
   * Test the registerProduct endpoint with null product data.
   */
  @Test
  public void testRegisterProductWithNullProduct() throws Exception {
    mockMvc.perform(post("/api/product/registry/registerProduct")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }


  /**
   * Test the updateProduct endpoint with valid product data.
   */
  @Test
  public void testUpdateProductWithValidProduct() throws Exception {
    String validProductJson = "{\"name\":\"Updated Product\",\"price\":150.0}";

    mockMvc.perform(post("/api/product/registry/updateProduct")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validProductJson))
        .andExpect(status().is3xxRedirection())
        .andExpect(jsonPath("$.name").value("Updated Product"))
        .andExpect(jsonPath("$.price").value(150.0));
  }


  /**
   * Test the updateProduct endpoint with invalid product data.
   */
  @Test
  public void testUpdateProductWithInvalidProduct() throws Exception {
    String invalidProductJson = "{\"name\":\"\"}";

    mockMvc.perform(post("/api/product/registry/updateProduct")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidProductJson))
        .andExpect(status().isBadRequest());
  }


  /**
   * Test the updateProduct endpoint with null product data.
   */
  @Test
  public void testUpdateProductWithNullProduct() throws Exception {
    mockMvc.perform(post("/api/product/registry/updateProduct")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }


  /**
   * Test the deleteProduct endpoint with valid product data.
   */
  @Test
  public void testDeleteProductWithValidProduct() throws Exception {
    String validProductJson = "{\"name\":\"Product to Delete\",\"price\":100.0}";

    mockMvc.perform(post("/api/product/registry/deleteProduct")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validProductJson))
        .andExpect(status().is3xxRedirection())
        .andExpect(jsonPath("$.name").value("Product to Delete"))
        .andExpect(jsonPath("$.price").value(100.0));
  }


  /**
   * Test the deleteProduct endpoint with invalid product data.
   */
  @Test
  public void testDeleteProductWithInvalidProduct() throws Exception {
    String invalidProductJson = "{\"name\":\"\"}";

    mockMvc.perform(post("/api/product/registry/deleteProduct")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidProductJson))
        .andExpect(status().isBadRequest());
  }

  /**
   * Test the deleteProduct endpoint with null product data.
   */
  @Test
  public void testDeleteProductWithNullProduct() throws Exception {
    mockMvc.perform(post("/api/product/registry/deleteProduct")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}