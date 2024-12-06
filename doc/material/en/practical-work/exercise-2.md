# Practical work Exercise 2: Fix quality issues of the product registry microservice (Write side)

The goal of this exercise is to fix the quality issues of the product registry microservice. You will learn how to improve the application's design and implementation quality.

**scope**: product registry microservice (of-product-registry-microservices:product.registry)

**estimated time**: 2 hours

**difficulty**: intermediate

## Task 1: Complete comments and Javadoc

The product registry microservice is a write-side microservice that manages the products available for sale and their availability for addition in a catalog.

The microservice is not completely documented. You must complete the comments and Javadoc of the microservice.

## Task 2: Fix lazy RuntimeException

RuntimeExceptions are not recommended because they are too generic. You must replace the lazy RuntimeExceptions with more specific exceptions.

::: code-group
```java:line-numbers=216 {12} [ProductRegistryCommandResource.java]
try {
  // Define the channel name, topic and schema for the consumer
  final String channelName = ProductRegistryEventChannelName.PRODUCT_REGISTRY_EVENT.toString();
  final String topic = channelName + "-" + correlationId;
  // Create and return the subscription (consumer)
  return pulsarClients.getClient(channelName)
      .newConsumer(Schema.JSON(ProductRegistryEvent.class))
      .subscriptionName(topic)
      .topic(topic)
      .subscribe();
} catch (PulsarClientException e) {
  throw new RuntimeException("Failed to create consumer for product registry events.", e); // This is not okay!
}
```
:::

## Task 3: Convert Entities (Persistence layer) payloads to records

The product registry microservice uses entities to persist events. You must convert the entities payloads from classes to records to improve the code quality.

::: code-group
```java:line-numbers=6 {4} [ProductRegisteredEventEntity.java]
/**
 * Payload for the event.
 */
public static class Payload { // Convert to record
  /**
   * The id of the product.
   */
  public String productId;
  /**
   * The name of the product.
   */
  public String name;
  /**
   * The description of the product.
   */
  public String productDescription;
}
```
:::

## Task 4: Modify Entities (Persistence layer) to use private fields with accessors

The product registry microservice uses entities to persist events. You must modify the entities to use private fields with accessors to improve the code quality.

::: code-group
```java:line-numbers=24 {4} [ProductRegisteredEventEntity.java]
/**
 * The payload for the event.
 */
public Payload payload; // Should be changed to private and have accessors
```
:::

::: warning
Verify that this operation does not brake mappers.
:::

## Task 4a: Public fields on Panache entities

SonarLint may report warnings about public fields on [Panache](https://quarkus.io/guides/mongodb-panache) entities. Determine if the warnings are relevant and justify your answer.

## Task 5: Fix errors and warnings reported by SonarLint

If not already done, install SonarLint in your IDE. Analyze the product registry microservice with SonarLint and fix the errors and warnings reported by SonarLint.

Be careful to treat only necessary issues. For example, you can ignore `field level injection` warnings due to Quarkus standards (cf [Quarkus documentation](https://quarkus.io/guides/cdi#what-does-a-bean-look-like)).

## Task 6: Add integration tests

Add an integration tests class for class `ProductRegistryCommandResource`. The goal is to test integration between client and server through the HTTP API.
The integration tests must cover the following cases:

- `POST /api/product/registry/registerProduct`: test the registration of a product
  - Test the registration of a product with a valid product
    - Response must be a redirection to the PR event stream
  - Test the registration of a product with an invalid product (missing field/invalid DTO)
    - Response must be a bad request
  - Test the registration of a product with a null product (no body)
    - Response must be a bad request
- `POST /api/product/registry/updateProduct`: test the update of a product
  - Test the update of a product with a valid product
    - Response must be a redirection to the PR event stream
  - Test the update of a product with an invalid product (missing field/invalid DTO)
    - Response must be a bad request
  - Test the update of a product with a null product (no body)
    - Response must be a bad request
- `POST /api/product/registry/deleteProduct`: test the deletion of a product
  - Test the deletion of a product with a valid product
    - Response must be a redirection to the PR event stream
  - Test the deletion of a product with an invalid product (missing field/invalid DTO)
    - Response must be a bad request
  - Test the deletion of a product with a null product (no body)
    - Response must be a bad request

::: tip
Code modifications may be necessary to make the tests pass.

For integration testing, see the [Quarkus documentation](https://quarkus.io/guides/getting-started-testing).
:::

## Task 7: Add unit tests

Add a unit tests class for class `ProductRegistry`. The goal is to test the business logic of the microservice.
The unit tests must cover the following cases:

- `handle` method: test the handling of a product registry command
  - Test the handling of a valid product registry command
    - The product must be registered
    - The product must be updated
    - The product must be removed
  - Test the handling of a null product registry command
    - The command must fail
- `apply` method: test the application of a product registry event
  - Test the application of a valid product registry event
    - The product must be registered
    - The product must be updated
    - The product must be removed
  - Test the application of a null product registry event
    - The event must fail
- `getVersion` method: test the version of the product registry
  - Test the version of the product registry
    - The version must be the same as the one set
  - Test the version of the product registry after an update
    - The version must have been incremented
- `hasProductWithId` method: test the presence of a product with an ID
  - Test the presence of a product with an existing ID
    - The product must be found
  - Test the presence of a product with a non-existing ID
    - The product must not be found
- `hasProduct` method: test the presence of a product
  - Test the presence of a product with an existing product
    - The product must be found
  - Test the presence of a product with a non-existing product
    - The product must not be found
- `isProductNameAvailable` method: test the availability of a product name
  - Test the availability of a product name with an available name
    - The name must be available
  - Test the availability of a product name with an unavailable name
    - The name must be unavailable

::: tip
You may need to use mocking to isolate the unit tests. For mocking usage of Quarkus CDI Beans, see the [Quarkus documentation](https://quarkus.io/guides/getting-started-testing#mock-support).
:::