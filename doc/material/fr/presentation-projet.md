# Présentation du projet Order flow

Ce document présente le projet order flow. Il décrit le contexte, les objectifs et le but du projet.

## Contexte

Le projet order flow est une application simplifiée de plateforme de commerce électronique qui permet aux clients de passer des commandes de produits ou de services. Il repose sur plusieurs microservices pour gérer le cycle de vie des commandes ainsi que les domaines connexes.

## Objectifs

Les objectifs du projet order flow sont de vous aider à comprendre les concepts suivants :
- Architecture microservices
- Conception pilotée par le métier (DDD)
- Architecture pilotée par les événements
- Concepts de qualité en ingénierie logicielle

## But

Le but du projet order flow est de vous fournir un travail pratique qui vous aidera à :
- Comprendre l'application order flow et ses microservices
- Améliorer la qualité de la conception et de l'implémentation de l'application
- Appliquer les concepts de qualité en ingénierie logicielle
- Préparer un support d'évaluation

## Application order flow

::: warning
Ne tentez pas d'apprendre tous les détails de l'application order flow ni de la conception pilotée par le métier en une seule fois. Concentrez-vous plutôt sur la compréhension des concepts principaux et de leur implémentation dans les microservices.
:::

::: tip
N'hésitez pas à poser des questions si vous avez besoin de plus d'informations.
:::

## Domaine métier

L'application order flow repose sur un domaine principal `Shopping en ligne` qui englobe plusieurs domaines principaux, de support et génériques (cf https://alexsoyes.com/ddd-domain-driven-design/) (cf `/doc/business/domain.cml`):

- Domaines principaux :
  - Panier d'achat
  - Traitement des commandes
- Domaines de support :
  - Registre des produits
  - Catalogue de produits
  - Gestion des stocks
  - Gestion des clients
- Domaines génériques :
  - Notification
  - Sourcing d'événements
  - Monnaie

## Cartographie des contextes

La cartographie des contextes de l'application order flow est représentée par le diagramme suivant (cf `/doc/business/context-mapping.cml`):

![Cartographie des contextes](../assets/images/context-mapping_ContextMap.svg)

## Architecture des microservices et des bibliothèques

L'application order flow est composée de plusieurs microservices implémentant les domaines selon un modèle CQRS et piloté par les événements.

- Services principaux :
  - Service de panier d'achat :
    - Agrégats
    - Commandes
    - Requêtes
  - Service de traitement des commandes :
    - Agrégats
    - Commandes
    - Requêtes
- Services de soutien :
  - Service de registre des produits :
    - Agrégat (unique : il n'y a qu'un seul registre pour simplifier)
    - Commandes
    - Requêtes
  - Service de catalogue de produits
    - Agrégats
    - Commandes
    - Requêtes
  - Service de gestion des stocks
    - Agrégat (unique : il n'y a qu'un seul stock pour simplifier)
    - Commandes
    - Requêtes
  - Service de gestion des clients
    - Agrégats
    - Commandes
    - Requêtes
- Services génériques :
  - Service de notification
    - Commandes
    - Requêtes
  - Sourcing d'événements :
    - Interface d'événement
    - Interface de stockage d'événements
  - Monnaie :
    - Objet-valeur

## Messages, sourcing d'événements, CQRS

L'application order flow repose sur l'échange de messages et le sourcing d'événements pour communiquer entre les microservices.

### Système d'échange de message

La communication par échange de message est un système d'envoi de messages entre microservices. Elle est utilisée pour communiquer entre microservices de manière découplée. Vous êtes probablement familier avec la communication synchrone (API REST HTTP) mais l'échange de message est asynchrone et fonctionne différemment.

L'échange de message fonctionne avec un courtier de messages (broker) qui est responsable de recevoir, stocker et transmettre les messages. Le courtier de messages est un middleware qui permet aux microservices de communiquer entre eux.

Lorsqu'un composant du système envoie un message, il l'envoie au courtier de messages. Le courtier de messages transmet ensuite le message au microservice destinataire en fonction d'une configuration spécifique. Le microservice destinataire traite le message et envoie une réponse au courtier de messages. Le courtier de messages transmet ensuite la réponse à tous les composants du système intéressés, qu'ils soient l'expéditeur ou non.

#### Courtier de messages

Le courtier de messages utilisé dans l'application order flow est Apache Pulsar. Apache Pulsar est une plateforme de messagerie distribuée et de streaming d'événements utilisée pour envoyer et recevoir des messages entre microservices.

Une particularité d'Apache Pulsar est qu'il ne supporte pas nativement le modèle de requête-réponse. Au lieu de cela, il utilise un modèle de publication-abonnement où un producteur envoie des messages à dans un Topic et un consommateur reçoit des messages depuis un topic. Pour le modèle de requête-réponse, vous devez l'implémenter vous-même. Cela se fait en utilisant un ID de corrélation dans le message.

### Sourcing d'événements

Le sourcing d'événements est un modèle utilisé pour stocker l'état d'une application sous forme de séquence d'événements. Il est utilisé pour stocker l'état de l'application de manière compréhensible et facile à modifier.

Lorsqu'un événement est créé, il est stocké dans un magasin d'événements. Le magasin d'événements est une base de données qui stocke les événements de manière à ce qu'ils soient faciles à interroger et à modifier. Lorsqu'un nouvel événement est créé, il est ajouté au magasin d'événements. Cela vous permet de voir l'historique de l'application et de voir comment l'application a évolué au fil du temps.

Le sourcing d'événements est utilisé dans l'application order flow pour stocker l'état des agrégats et des entités. Lorsqu'une commande est reçue par un microservice, elle est traitée et un événement est créé. L'événement est ensuite stocké dans le magasin d'événements. Si une commande est reçue sur un autre composant, il peut lire le magasin d'événements pour obtenir l'état actuel de l'application. C'est donc un moyen de partager l'état de l'application entre les microservices et de maintenir la cohérence de l'état.

Dans l'application order flow, le sourcing d'événements est implémenté avec des collections de documents NoSQL comme magasins d'événements, en utilisant MongoDB.

### CQRS

En plus du sourcing d'événements, l'application order flow utilise le modèle CQRS. CQRS signifie Command Query Responsibility Segregation. C'est un modèle utilisé pour séparer les opérations de lecture et d'écriture d'une application.

Les avantages de CQRS sont qu'il permet d'optimiser séparément les opérations de lecture et d'écriture, de faire évoluer indépendamment les opérations de lecture et d'écriture, et de simplifier le code en séparant les opérations de lecture et d'écriture.

Bien que CQRS soit un modèle puissant, il est également complexe et peut être difficile à implémenter, surtout dans un système distribué et asynchrone en ce qui concerne la cohérence : dans un tel système, la cohérence est éventuelle et non immédiate. Cela signifie que l'état de l'application n'est pas immédiatement cohérent entre les microservices mais le sera à un moment donné dans le futur.

Mais un autre avantage de CQRS est qu'il compense la lenteur du sourcing d'événements pour la reconstruction de l'état en rejouant les événements du magasin d'événements en utilisant un modèle de lecture alternatif qui stocke l'état de l'application de manière plus facile à interroger.

Dans l'application order flow, le modèle CQRS est implémenté en utilisant le système de messagerie Pulsar pour les commandes et les requêtes, et MongoDB pour le modèle de lecture.

[Informations supplémentaires sur CQRS (Martin Fowler)](https://martinfowler.com/bliki/CQRS.html)
[Comment appliquer le modèle CQRS étape par étape (Daniel Whittaker)](https://danielwhittaker.me/2020/02/20/cqrs-step-step-guide-flow-typical-application/)
