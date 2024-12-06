# Practical Work exercise 5: Centralize logs and enable monitoring

The goal of this exercise is to show you how to centralize logs and enable monitoring in the order flow application.

Observability is a key aspect of software development. It allows you to understand the behavior of your application and to detect issues quickly. Centralizing logs and enabling monitoring are two important aspects of observability because they provide you with insights into the behavior of your application in a centralized way without relying on several sources, tools or user interfaces.

::: tip
You can validate implementation steps by asking questions to the teacher.
:::

**estimated time**: 1 hour

**difficulty**: intermediate

## Task 0: Verify the logging in application

Your first preliminary step is to verify the logging in the application. You must check the logs produced by the application and understand how they are generated. You may also verify that each application part produces logs in a consistent way (especially in the business logic, which is the most important scope).

Refer to the [Quarkus logging documentation](https://quarkus.io/guides/logging) to understand how to configure logging in a Quarkus application.

## Task 1: Logs production

In a Quarkus application, logs are produced by the application and can be configured to be sent to a specific output. By default, logs are sent to the console.

You must configure the application to send logs to a file so it will be collectable by a dedicated tool.

Refer to the [Quarkus logging documentation](https://quarkus.io/guides/logging#file-log-handler) about file logging.

You will have to:
- Configure the application to send logs to a file.
- Set a common log format for all logs.

## Task 2: Logs centralization

Logs are produced by the application and can be sent to a specific output. By default, logs are sent to the console or a file.

Now, you are going to use a combination of tools to centralize logs produced by the application. You will use the following tools:
- [Grafana Loki](https://grafana.com/oss/loki/): a horizontally-scalable, highly-available, multi-tenant log aggregation system inspired by Prometheus. (Check the getting started guide [here](https://grafana.com/docs/loki/latest/get-started/?pg=oss-loki&plcmt=resources))
- [Grafana Alloy](https://grafana.com/docs/alloy/latest/?pg=oss-loki&plcmt=resources): a fully managed, scalable, and secure observability platform that enables you to visualize, alert, and query your logs, metrics, and traces with Graf
- [Grafana](https://grafana.com/): a multi-platform open-source analytics and interactive visualization web application.

The goal is to let your Quarkus application produce logs which will be picked up by Promtail and sent to Loki. Then, you will be able to visualize the logs in Grafana with automatic log labels, tagging and indexing.

You will have to:
- Install Grafana Loki and Grafana Alloy as devcontainer services.
- Configure Alloy to set the right labels and send logs to Loki.
- Log in to Grafana and create a new datasource for Loki.

## Task 3: Explorer real-time Monitoring

Now that you have centralized logs, you can explore real-time monitoring with Grafana.

Take some screenshots of the logs produced by the application in Grafana. You can also create a dashboard to visualize the logs in real-time. Add some explanations about the logs produced by the application.