# TP Exercice 4: Collaborez sur le monorepo

L'objectif de cet exercice est de collaborer sur le monorepo de l'application order flow. Vous devrez travailler ensemble sur la même base de code et gérer les dépendances entre les microservices.

Les tâches suivantes sont conçues pour que vous effectuiez les mêmes opérations sur vos parties respectives de la base de code. Vous devrez communiquer avec les membres de votre équipe pour vous assurer que les modifications sont cohérentes dans tout le monorepo.

::: tip
Pour valider les étapes de mise en œuvre, vous pouvez demander à l'autre équipe de revoir vos modifications.
Assurez-vous de communiquer avec votre enseignant si vous avez besoin d'une deuxième validation.
:::

::: warning
Attention aux collisions avec les modifications des membres de l'équipe. Vous devez communiquer tout au long du processus pour vous assurer que les modifications sont cohérentes dans tout le monorepo et ne sont pas en conflit. Vous pouvez utiliser UML pour décrire également votre solution et décider de la meilleure façon de la mettre en œuvre.

Vous pourriez rencontrer des situations où des modifications préliminaires sont nécessaires de la part d'une personne/équipe spécifique pour permettre à l'autre de mettre en œuvre ses modifications. Essayez alors de planifier les modifications de manière à pouvoir travailler en parallèle au maximum.
:::

**temps estimé** : 2 heures

**difficulté** : intermédiaire

## Tâche 1 : Meilleure gestion des erreurs asynchrones

Les microservices n'utilisent pas de mécanisme de gestion des erreurs. Vous devez améliorer le mécanisme de gestion des erreurs pour propager les erreurs à l'appelant (actuellement API Gateway).

Vous devez ajouter une classe pour représenter une erreur de traitement. Cela implique que `ProductRegistryEvent` n'est pas la seule sortie des messages de canaux. Une sortie d'erreur doit être ajoutée aux messages de canaux possibles en plus des événements eux-mêmes.

Pour ce faire, vous devez :

- Créer une classe `ProductRegistryError` pour représenter une erreur de traitement générique
- Ajouter une interface scellée pour représenter les messages de canal
- Ajouter les `ProductRegistryEvent` et `ProductRegistryError` aux messages de canal
- N'oubliez pas de mettre à jour les mappages de types Json

::: code-group

```java:line-numbers=6 {4-13} [ProductRegistryEvent.java (avec mappages de types Json)]
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
Attention à mettre à jour les mappages de types Json pour inclure la nouvelle classe `ProductRegistryError`.
:::

## Tâche 2 : Revoir la lecture directe du flux d'événements

Comme vu dans l'analyse, le flux d'événements n'est pas correctement implémenté. Le flux d'événements ne doit pas retourner d'événements sur le côté écriture.

L'opération souhaitée dans CQRS est appelée "Lecture depuis le primaire" : elle permet de lire instantanément les objets produits par le côté écriture dès qu'ils sont produits. Cependant, le côté écriture ne doit pas permettre la lecture des événements et l'opération doit être déléguée au côté lecture. Ensuite, le point de terminaison de lecture sur API Gateway doit interroger le côté lecture à la place.

Pour ce faire, vous devez (Exemple avec le registre de produits) :

- Supprimer l'envoi d'événements sur le côté écriture (commenter la méthode qui produit l'événement sur le bus de synchronisation avec l'identifiant de corrélation)
- Modifier le point de terminaison de lecture sur API Gateway pour interroger le côté lecture à la place (modifier uniquement la sémantique du sujet et de l'abonnement Pulsar)
- Mettre à jour l'`Emitter` pour passer l'identifiant de corrélation au côté lecture afin que le côté lecture puisse être utilisé pour sélectionner les bons événements
- Utiliser le `ProductRegistryEventConsumer` pour ajouter la publication du flux d'événements de la même manière qu'elle était réalisée sur le côté écriture auparavant : vous pouvez alors simplement rassembler l'ancienne logique de `ProductRegistryEventEmitter` et la mettre dans le bon espace de noms et la méthode sous le microservice côté lecture.

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

## Tâche 2bis : Délai d'attente de lecture et refactorisation du point de terminaison de lecture

Vous avez peut-être remarqué que le point de terminaison de lecture sur API Gateway a un délai d'attente codé en dur. Vous devez ajouter un délai d'attente en tant que propriété d'application au point de terminaison de lecture sur API Gateway pour permettre la définition du délai d'attente via la configuration (cf [Configuration de l'application Quarkus](https://quarkus.io/guides/config#inject-the-configuration)).

De plus, vous devez refactoriser le point de terminaison de lecture sur API Gateway pour réduire la complexité de la méthode et la rendre plus lisible. N'hésitez pas à externaliser les abstractions dans un code partagé pour les rendre disponibles à d'autres microservices à l'avenir.

Pour ce faire :

- créer un nouveau répertoire de projet gradle `libs/shared` dans le monorepo
- copier un fichier build.gradle d'un autre microservice et l'adapter au code partagé
- Ici, vous n'avez pas besoin de la plupart des dépendances quarkus, vous pouvez les supprimer
- Vous pouvez utiliser le modèle d'interface fonctionnelle pour créer des abstractions réutilisables pour le point de terminaison de lecture

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

## Tâche 3 : Fusionner les modifications

Vous devez maintenant fusionner les modifications des deux équipes pour vous assurer que les modifications sont cohérentes dans tout le monorepo.
N'oubliez pas de signaler vos demandes de fusion pour évaluation.

Votre processus doit être propre :

- Créer une branche pour chaque tâche/fonctionnalité
- Assurez-vous d'avoir un historique propre
- Squashez les commits si nécessaire
- Assurez-vous d'avoir une description de merge request propre
- Suivez bien le processus que vous avez établi dans l'exercice précédent

## Tâche 4 : Préparer un patch

Utilisez votre stratégie de embranchement pour préparer un patch pour les modifications que vous avez apportées.

Incrémentez la version des parties de la base de code que vous avez modifiées.

Utilisez la version sémantique pour incrémenter les numéros de version.

## Tâche 5 : Mettre à jour la documentation

Vous devez mettre à jour la documentation appropriée dans le monorepo pour refléter les modifications que vous avez apportées.

## Tâche 5bis : Ajouter un diagramme de séquence

Vous devez ajouter un diagramme de séquence pour décrire le flux du flux d'événements dans l'application et matérialiser les opérations et interactions lors de l'ajout d'un produit au registre. Utilisez UML pour décrire le flux d'événements.

## Tâche 6 : Taguer votre patch

Taguez votre patch avec le numéro de version que vous avez choisi et ajoutez une note de version pour décrire les modifications que vous avez apportées.
