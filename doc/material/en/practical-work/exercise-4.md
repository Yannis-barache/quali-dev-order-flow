# Practical work exercise 4: Collaborate on the monorepo

The goal of this exercise is to collaborate on the monorepo of the order flow application. You will have to work together on the same codebase and manage the dependencies between the microservices.

Following tasks are designed for you to do same operations on your respective codebase parts. You will have to communicate with your team members to ensure that the changes are consistent across the monorepo.

::: tip
To validate implementation steps, you can ask the other team to review your changes.
Make sure to communicate with your teacher if you need a second validation review.
:::

::: warning
Be careful to colision with team members changes. You must communicate along the way to ensure that the changes are consistent across the monorepo and not conflicting. You can make use of UML to also describe your solution and decide on the best way to implement it.

You could encounter situations where preliminary changes are required from one specific person/team to allow the other to implement their changes. Try then to plan the changes in a way that will allow you to work in parallel at most.
:::

**estimated time**: 2 hours

**difficulty**: intermediate

## Task 1: Better async error handling

The microservices does not use error handling mechanism. You must improve the error handling mechanism to propagate errors to the caller (actually API Gateway).

You must add a class to represent a processing error. It implies that `ProductRegistryEvent` is not the only output of the channel messages. An output error should be added to the channel messages in addition to events.

To achieve this, you must:
- Create a class `ProductRegistryError` to represent a processing error
- Add a sealed interface to represent the channel messages
- Add the `ProductRegistryEvent` and `ProductRegistryError` to the channel messages
- Don't forget to update Json type mappings

::: code-group
```java:line-numbers=6 {4-13} [ProductRegistryEvent.java (with Json type mappings)]
/**
 * Base class for product registry events.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ProductRegistered.class, name = "ProductRegistered"),
    @JsonSubTypes.Type(value = ProductUpdated.class, name = "ProductUpdated"),
    @JsonSubTypes.Type(value = ProductRemoved.class, name = "ProductRemoved")
})
public sealed interface ProductRegistryEvent permits ProductRegistered, ProductRemoved, ProductUpdated {
}
```
:::
::: warning
Be careful to update the Json type mappings to include the new `ProductRegistryError` class.
:::

## Task 2: Rework the event stream direct read

As seen in analysis, the event stream is not correctly implemented. The event stream should not return events on the write side. 

The wanted operation in CQRS is called "Read from Primary": it is meant to allow instantaneous read from objects produced by the write side as soon as they are produced. However, the write side should not allow the read of events and the operation should be delegated to the read side. Then, the read endpoint on API Gateway should query the read side instead.

To achieve this, you must (Example with Product Registry):

- Remove the event sinking on the write side (comment the method which produces the event on the sink bus with correlation id)
- Modify the read endpoint on API Gateway to query the read side instead (modify only the semantic of Pulsar topic and subscription)
- Update the `Emitter` to pass the correlation id to the read side so the read side can query be used to select the right events
- Make use of the `ProductRegistryEventConsumer` to add event stream publication the same way it was achieved on the write side before: you can then simply gather the `ProductRegistryEventEmitter` old logic and put it in the right namespace and method under the read side microservice.

::: code-group
```java:line-numbers=70 {5} [ProductRegistryCommandConsumer.java]
return loadRegistry().handle(cmd)
    .subscribeAsCompletionStage()
    .thenAccept(evt -> {
      // Produce event on correlated bus
      eventProducer.sink(correlationId, evt);
      Log.debug(String.format("Acknowledge command: %s", cmd.getClass().getName()));
      msg.ack();
    }).exceptionallyCompose(e -> {
      // Log error and nack message
      Log.error(String.format("Failed to handle command: %s", e.getMessage()));
      msg.nack(e);
      return CompletableFuture.failedFuture(e);
    });
```
```java:line-numbers=84 [ProductRegistryEventEmitter.java]
/**
 * Produce the given event with the given correlation id.
 * 
 * @param correlationId - the correlation id
 * @param event         - the event
 */
public void sink(String correlationId, ProductRegistryEvent event) {
  // Get the producer for the correlation id
  getEventSinkByCorrelationId(correlationId)
      .thenAccept((producer) -> {
        // Sink the event
        producer
            .newMessage()
            .value(event)
            .sendAsync()
            .whenComplete((msgId, ex) -> {
              if (ex != null) {
                throw new RuntimeException("Failed to produce event for correlation id: " + correlationId, ex);
              }
              Log.debug(String.format("Sinked event with correlation id{%s} in msg{%s}", correlationId, msgId));
              try {
                producer.close();
              } catch (PulsarClientException e) {
                throw new RuntimeException("Failed to close producer", e);
              }
            });
      });
}
```
```java:line-numbers=94 {14} [ProductRegistryCommandResource.java]
/**
 * Endpoint to stream product registry registered events.
 * 
 * @param correlationId - correlation id to use for the consumer
 * @return Multi of product registry events
 */
@GET
@Path("/events/productRegistered")
@RestStreamElementType(MediaType.APPLICATION_JSON)
public Multi<ProductRegisteredEventDto> registeredEventStream(@QueryParam("correlationId") String correlationId) {
  // Create a stream of product registry events
  return Multi.createFrom().emitter(em -> {
    // Create consumer for product registry events with the given correlation id
    final Consumer<ProductRegistryEvent> consumer = getEventsConsumerByCorrelationId(correlationId);
    // Close the consumer on termination
    em.onTermination(() -> {
      try {
        consumer.unsubscribe();
      } catch (PulsarClientException e) {
        Log.error("Failed to close consumer for product registry events.", e);
      }
    });
    // Consume events and emit DTOs
    CompletableFuture.runAsync(() -> {
      while(!em.isCancelled()) {
        try {
          final var timeout = 10000;
          final var msg = Optional.ofNullable(consumer.receive(timeout, TimeUnit.MILLISECONDS));
          if (msg.isEmpty()) {
            // Complete the emitter if no event is received within the timeout. Free up resources.
            Log.debug("No event received within timeout of " + timeout + " seconds.");
            em.complete();
          }
          final ProductRegistryEvent evt = msg.get().getValue();
          Log.debug("Received event: " + evt);
          // Map event to DTO
          if (evt instanceof ProductRegistered registered) {
            Log.debug("Emitting DTO for registered event: " + registered);
            // Emit DTO for registered event
            em.emit(ProductRegistryEventDtoMapper.INSTANCE.toDto(registered));
          } else {
            // Fail the stream on unexpected event types
            Throwable error = new ProductRegistryEventStreamException("Unexpected event type: " + evt.getClass().getName());
            em.fail(error);
            return;
          }
          // Acknowledge the message
          consumer.acknowledge(msg.get());
        } catch (PulsarClientException e) {
          Log.error("Failed to receive event from consumer.", e);
          em.fail(e);
          return;
        }
      }
    });
  });
}
```
:::

## Task 2a: Read timeout and read endpoint refactoring

You may have noticed that the read endpoint on API Gateway does have an hard coded timeout. You must add a timeout an application property to the read endpoint on API Gateway to allow the setting of timeout via configuration (cf [Quarkus application configuration](https://quarkus.io/guides/config#inject-the-configuration)).

In addition, you must refactor the read endpoint on API Gateway to reduce the complexity of the method and to make it more readable. Don't hesitate to externalize abstractions in shared code to make it available to other microservices in the future.

To achieve this :
- create a new gradle project directory `libs/shared` in the monorepo
- copy a build.gradle file from another microservice and adapt it to the shared code
- Here you don't need most of quarkus dependencies, you can remove them
- You can use the Functional Interface pattern to create reusable abstractions for the read endpoint

::: code-group
```java:line-numbers=103 {16-45} [ProductRegistryCommandResource.java]
public Multi<ProductRegisteredEventDto> registeredEventStream(@QueryParam("correlationId") String correlationId) {
  // Create a stream of product registry events
  return Multi.createFrom().emitter(em -> {
    // Create consumer for product registry events with the given correlation id
    final Consumer<ProductRegistryEvent> consumer = createEventsConsumerByCorrelationId(correlationId);
    // Close the consumer on termination
    em.onTermination(() -> {
      try {
        consumer.unsubscribe();
      } catch (PulsarClientException e) {
        Log.error("Failed to close consumer for product registry events.", e);
      }
    });
    // Consume events and emit DTOs
    CompletableFuture.runAsync(() -> {
      while(!em.isCancelled()) { // TODO: This loop can be extracted to a shared code
        try {
          final var timeout = 10; // TODO: This timeout must be controlled by configuration
          final var msg = Optional.ofNullable(consumer.receive(timeout, TimeUnit.SECONDS));
          if (msg.isEmpty()) {
            // Complete the emitter if no event is received within the timeout. Free up resources.
            Log.debug("No event received within timeout of " + timeout + " seconds.");
            em.complete();
          }
          final ProductRegistryEvent evt = msg.get().getValue();
          Log.debug("Received event: " + evt);
          // Map event to DTO
          if (evt instanceof ProductRegistered registered) {
            Log.debug("Emitting DTO for registered event: " + registered);
            // Emit DTO for registered event
            em.emit(ProductRegistryEventDtoMapper.INSTANCE.toDto(registered));
          } else {
            // Fail the stream on unexpected event types
            Throwable error = new ProductRegistryEventStreamException("Unexpected event type: " + evt.getClass().getName());
            em.fail(error);
            return;
          }
          // Acknowledge the message
          consumer.acknowledge(msg.get());
        } catch (PulsarClientException e) {
          Log.error("Failed to receive event from consumer.", e);
          em.fail(e);
          return;
        }
      }
    });
  });
}
```

## Task 3: Merge things together

You must now merge the changes from both teams to ensure that the changes are consistent across the monorepo.
Don't forget to report your merge requests for evaluation.

Your process must be clean :
- Create a branch for each task/feature
- Make sure to have a clean history
- Squash commits if necessary
- Make sure to have a clean merge request description
- Follow the process you chooses in the previous exercise

## Task 4: Prepare a patch

Use your branching strategy to prepare a patch for the changes you have made.

Increment the version of the codebase parts you modified.

Use semantic versioning to increment the version numbers.

## Task 5: Update the documentation

You must update the appropriate documentation in the monorepo to reflect the changes you have made.

## Task 5a: Add a sequence diagram

Add a sequence diagram to describe the event stream flow in application and materialize the operations and interactions while adding a product to the registry. Use the UML notation to describe the interactions between the microservices.

## Task 6: Tag your patch

Tag your patch with the version number you have chosen and add a release note to describe the changes you have made.