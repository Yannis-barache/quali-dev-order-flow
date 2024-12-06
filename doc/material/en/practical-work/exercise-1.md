# Practical work Exercise 1: Analyse the application

The goal of this exercise is to analyse the order flow application and its microservices. You will learn how to understand the main concepts and how they are implemented in the microservices.

**estimated time**: 1 hour

**difficulty**: beginner

## Task 1: Responsibility seggregation

The order flow application is a set of microservices that should implement several business domains. Compared to traditional monolithic applications, microservices are designed to be small and focused on a specific business domain. Moreover, as the application is using specific patterns, the seggregation of responsibilities can be finer than usual.

## Task 1: Questions

1. What are the main business domains of the order flow application?

*tip*: Check the Context mapping files in the `doc` folder.

2. How are the microservices designed to implement the business domains?

*tip*: Check the microservices architecture and packages names in `apps` and `libs` folders.

3. What are the reponsibilities of code containers `apps/of-api-gateway`, `apps/of-product-registry-microservices/product.registry`, `apps/of-product-registry-microservices/product.registry.read`, `libs/event-sourcing`, `libs/published-language`?

*tip*: You can observe Context mapping files in the `doc` folder as well as interpreting the code in the `apps` and `libs` folders.

## Task 2: Identify the main concepts

The order flow application is using specific patterns to implement the business domains. These patterns are based on Domain-Driven Design (DDD) and Event-Driven Architecture (EDA) principles.

You must identify the main concepts used in the order flow application.

## Task 2: Questions

1. What are the main concepts used in the order flow application?

2. How are the main concepts implemented in the microservices?

::: tip
Identify "moving parts" of the application, such as namespaces semantic and code structures. Identify if a specific concept or pattern is either directly implemented or a generic solution is used (e.g. a library, a framework, a database, a third-party service/software). You can also rely on observation of depedencies declaration files (cf. [Gradle](https://docs.gradle.org/current/userguide/userguide.html)).
:::

::: warning
This question requires you to make some search and to understand the code structure.
However, you can also rely on the [project presentation](../project-presentation) to help you clarify subjects of the question.
:::

3. What does the `libs/event-sourcing` library do? How is it used in the microservices (business and code structure relation wise)?

4. How is event-sourcing current implementation ensuring the reliability of the internal application states?

## Task 3: Identify the quality issues

The order flow application is not yet complete nor fully functional. It is up to you to improve the application's design and implementation quality and, if necessary, to implement the missing features.

You must identify the quality issues in the order flow application. To achieve this, you must use the following tools:
- [SonarLint](https://www.sonarlint.org/)
- Analyse the consistency of the code with the [project presentation](../project-presentation) and Context mapping
- Verify the semantic of the code with its business declaration and the coding standards

::: warning
You must not fix the quality issues in this exercise. The goal is to identify the quality issues and to understand how to improve the application's design and implementation quality. You can eventualy provide code snippets to illustrate your findings.
:::

::: tip
You can call the teacher if you need help to identify the quality issues. Don't hesitate to ask for verification on a regular basis during the sessions.
:::

::: tip
You can tweak the SonarLint configuration to match the project's coding standards (e.g. indentation, naming conventions, etc.). The project repo is relying on Quarkus and it implies some specific coding standards.
Also be careful to treat all issues reported by SonarLint, even if they are not directly related to the quality of the code (an explanation may be sufficient in some cases).
:::