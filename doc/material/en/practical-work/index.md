# Practical work objectives

The objectives of the practical work are to make you practice the following concepts:
- Microservices architecture
- Domain-driven design
- Event-driven architecture
- Quality improvements and refactoring
- Testing and test-driven development
- Continuous integration and continuous deployment
- Documentation and communication
- Teamwork and collaboration
- Code review, feedback and version control

::: warning
The goal of the practical work is to help you to understand concepts and to apply them in practice. It is not about delivering a perfect solution but to learn from your mistakes and to improve your skills.
:::

::: tip
Don't hesitate to ask questions if you need more information.
:::

## State of the application

The order flow application is a set of microservices that should implement several business domains.

However, for the sake of simplicity, the application is not yet complete nor fully functional.

### Implemented features

The following features are already implemented in the order flow application:

- Product Registry service: manages the products available for sale and availability in catalog.
- Event sourcing: event management interface for the microservices.
- Published language: language interface for the microservices. It only holds the objects related to registry for now.

### Missing features

The following features are missing in the order flow application:

- Product Catalog service: manages the products available for sale and their prices. It should have a materialized view of the products available for sale (with prices and aggregated information).
- Stock Management service: manages the stock of products available for sale. It needs a way to manage booking of items and stock availability.
- Shopping Cart service: manages the shopping cart of customers
- Order Processing service: manages the order lifecycle of customers
- Customer Management service: manages the customers and their orders
- Notification service: sends notifications to customers
- Money: money values interface for the microservices

## Practical work

::: warning
For a better understanding of the practical work material, check the [project presentation](../project-presentation) first.
:::

The practical work is divided into several exercises that will help you to understand the order flow application and its microservices. You will also learn how to improve the application's design and implementation quality.

::: tip
You are starting group work (3 to 4 students) only from exercise 3. Before that, you will work individually.
From exercise 4, start working on the same repository.
:::

The practical work is divided into the following exercises:

1. [Exercise 1: Analyse the application](./exercise-1.html) (1 hour)
2. [Exercise 2: Fix quality issues of the product registry microservice](./exercise-2) (2 hours)
3. [Exercise 3: Enable team collaboration](./exercise-3) (30 min)
4. [Exercise 4: Collaborate on the monorepo](./exercise-4) (2 hours)
5. [Exercise 5: Centralize logs and enable monitoring](./exercise-5) (1 hour)
<!-- 6. [Exercise 6: CI/CD for application building](./exercise-6) (1 hour) -->

<!-- Going further (optional):

7. [Exercise 7: CI/CD: leverage NX features for monorepo](./exercise-7)
8. [Exercise 8: Implement the product catalog materialized view](./exercise-8)
9. [Exercise 9: Implement the stock management booking feature](./exercise-9) -->

## What to deliver?

For each exercise, you will have to deliver the following items:
- A report in markdown format with your answers to the questions and the steps you have followed to complete the exercise.
- When applicable, the code changes you have made in the monorepo as a merge request (must be merged in your main branch).
- When applicable, your changes in documentation must be exported as images.
- When applicable, the tests you have written to validate your changes.

::: tip
- Take care to make your report clear and concise. Use diagrams, code snippets, and screenshots to illustrate your answers.
- Don't forget to include the references to the files you have modified and set correct access rights to your assets.
- Include in your main document to your teacher the links to your repository, merge requests, and documentation.
:::
