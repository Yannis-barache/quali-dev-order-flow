# TP Exercice 1: Analysez l'application

L'objectif de cet exercice est d'analyser l'application Order flow et ses microservices. Vous apprendrez à comprendre les concepts principaux et comment ils sont implémentés dans les microservices.

**temps estimé** : 1 heure

**difficulté** : débutant

## Tâche 1 : Ségrégation des responsabilités

L'application Order flow est un ensemble de microservices qui doivent implémenter plusieurs domaines métiers. Comparé aux applications monolithiques traditionnelles, les microservices sont conçus pour être petits et concentrés sur un domaine métier spécifique. De plus, comme l'application utilise des modèles spécifiques, la ségrégation des responsabilités peut être plus fine que d'habitude.

## Tâche 1 : Questions

1. Quels sont les principaux domaines métiers de l'application Order flow ?

*astuce* : Consultez les fichiers de cartographie de contexte dans le dossier `doc`.

2. Comment les microservices sont-ils conçus pour implémenter les domaines métiers ?

*astuce* : Consultez l'architecture des microservices et les noms des packages dans les dossiers `apps` et `libs`.

3. Quelles sont les responsabilités des conteneurs de code `apps/of-api-gateway`, `apps/of-product-registry-microservices/product.registry`, `apps/of-product-registry-microservices/product.registry.read`, `libs/event-sourcing`, `libs/published-language` ?

*astuce* : Vous pouvez observer les fichiers de cartographie de contexte dans le dossier `doc` ainsi qu'interpréter le code dans les dossiers `apps` et `libs`.

## Tâche 2 : Identifier les concepts principaux

L'application Order flow utilise des modèles spécifiques pour implémenter les domaines métiers. Ces modèles sont basés sur les principes de la conception pilotée par le domaine (DDD) et de l'architecture pilotée par les événements (EDA).

Vous devez identifier les concepts principaux utilisés dans l'application Order flow.

## Tâche 2 : Questions

1. Quels sont les concepts principaux utilisés dans l'application Order flow ?

2. Comment les concepts principaux sont-ils implémentés dans les microservices ?

::: astuce
Identifiez les "parties mobiles" de l'application, telles que la sémantique des espaces de noms et les structures de code. Identifiez si un concept ou un modèle spécifique est directement implémenté ou si une solution générique est utilisée (par exemple, une bibliothèque, un framework, une base de données, un service/logiciel tiers). Vous pouvez également vous appuyer sur l'observation des fichiers de déclaration de dépendances (cf. [Gradle](https://docs.gradle.org/current/userguide/userguide.html)).
:::

::: avertissement
Cette question nécessite de faire des recherches et de comprendre la structure du code.
Cependant, vous pouvez également vous appuyer sur la [présentation du projet](../presentation-projet) pour vous aider à clarifier les sujets de la question.
:::

3. Que fait la bibliothèque `libs/event-sourcing` ? Comment est-elle utilisée dans les microservices (relation entre métier et structure du code) ?

4. Comment l'implémentation actuelle de l'event-sourcing assure-t-elle la fiabilité des états internes de l'application ?

## Tâche 3 : Identifier les problèmes de qualité

L'application Order flow n'est pas encore complète ni entièrement fonctionnelle. Il vous incombe d'améliorer la conception et la qualité de l'implémentation de l'application et, si nécessaire, d'implémenter les fonctionnalités manquantes.

Vous devez identifier les problèmes de qualité dans l'application Order flow. Pour ce faire, vous devez utiliser les outils suivants :
- [SonarLint](https://www.sonarlint.org/)
- Analyser la cohérence du code avec la [présentation du projet](../presentation-projet) et la cartographie de contexte
- Vérifier la sémantique du code avec sa déclaration métier et les standards de codage

::: avertissement
Vous ne devez pas corriger les problèmes de qualité dans cet exercice. L'objectif est d'identifier les problèmes de qualité et de comprendre comment améliorer la conception et la qualité de l'implémentation de l'application. Vous pouvez éventuellement fournir des extraits de code pour illustrer vos conclusions.
:::

::: astuce
Vous pouvez appeler le professeur si vous avez besoin d'aide pour identifier les problèmes de qualité. N'hésitez pas à demander une vérification régulière pendant les sessions.
:::

::: astuce
Vous pouvez ajuster la configuration de SonarLint pour correspondre aux standards de codage du projet (par exemple, indentation, conventions de nommage, etc.). Le dépôt du projet repose sur Quarkus et cela implique certains standards de codage spécifiques.
Faites également attention à traiter tous les problèmes signalés par SonarLint, même s'ils ne sont pas directement liés à la qualité du code (une explication peut être suffisante dans certains cas).
:::