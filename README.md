# Ticho-Boot Project Description

Ticho-Boot is a deeply integrated Spring Boot development framework designed for enterprise-level applications. Through
modular architecture and standardized component encapsulation, it significantly accelerates the rapid construction of
Spring Boot applications. Built on cutting-edge technology stacks, the framework offers out-of-the-box functional
components and development toolchains, empowering developers to focus on core business logic implementation.

* Runtime Environment: JDK 17 LTS (Long-Term Support) Version
* Core Framework: Spring Boot 3.4.4 (Compatible with Spring Framework 6.1.6)
* Distributed Architecture: Spring Cloud 2023.0.3 + Spring Cloud Alibaba 2023.0.3.2
* Security System: Deep integration with Spring Security 6.4.4
* Persistence Layer: Mybatis-Plus 3.5.11 + MySQL 8.0 Driver
* Core Modules:

1. [ticho-dependencies](ticho-dependencies) encapsulates global dependency version management, providing Maven parent
   POM configuration to simplify version control for submodules.
2. [ticho-tool](ticho-tool) encapsulates commonly used utility classes such as code generation, JSON serialization, and
   intranet penetration implementation via Netty.
3. [ticho-starter](ticho-starter) encapsulates Spring Boot-related functional components, utility classes, and
   standardized Starters, enabling developers to easily integrate these features into their applications.

---

## 📂 Project Structure Overview

### 1. **ticho-dependencies**

- **Dependency Management Module**  
  Manages global dependency versions uniformly through Maven parent POM configuration, simplifying version control for
  submodules.

---

### 2. **ticho-starter**

Spring Boot Starter modules, ready to use out-of-the-box:

- **ticho-starter-cache**  
  Caching abstraction layer supporting hybrid strategies for local and distributed caching (e.g., Redis).
- **ticho-starter-cloud**  
  The core components of microservices integrate components such as service discovery and configuration center.
- **ticho-starter-email**  
  Email service encapsulation with templated email sending support.
- **ticho-starter-es**  
  ElasticSearch rapid integration, providing DSL builders and efficient query tools.
- **ticho-starter-gateway**  
  Dynamic gateway supporting API governance features like routing, rate limiting, and authentication.
- **ticho-starter-health**  
  Enhanced health checks exposing application status and custom probe interfaces.
- **ticho-starter-http**  
  Enhanced Feign client with default OkHttp support, load balancing, and custom interceptors.
- **ticho-starter-log**  
  Distributed log tracing integrating MDC and log tagging.
- **ticho-starter-minio**  
  Object storage service supporting S3 protocol operations for MinIO.
- **ticho-starter-rabbitmq**  
  Enhanced RabbitMQ message queue capabilities.
- **ticho-starter-redis**  
  Simplified Redis configuration and utility encapsulation.
- **ticho-starter-redisson**  
  Distributed locks and concurrency control based on Redisson.
- **ticho-starter-security**  
  Security authentication module supporting OAuth2/JWT and dynamic permission management.
- **ticho-starter-swagger**  
  Automated API documentation integrating Swagger2 and knife4j.
- **ticho-starter-view**  
  Unified view rendering.
- **ticho-starter-web**  
  Web layer enhancements including global exception handling, parameter validation, and CORS configuration.

---

### 3. **ticho-tool**

**Core utility library**, framework-independent:

- **ticho-tool-generator**  
  Code generator using Beetl templates to dynamically generate CRUD code.
- **ticho-tool-intranet**  
  Intranet penetration tool implementing NAT-based internal service exposure via Netty.
- **ticho-tool-json**  
  Jackson-based implementation providing simplified utility classes.

---

### 4. **ticho-trace**

**Full-link tracing system** compatible with OpenTracing standards:

- **ticho-trace-common**  
  Common model definitions (Span, TraceID, etc.).
- **ticho-trace-core**  
  Trace context management supporting log correlation and propagation.
- **ticho-trace-feign**  
  Feign client interceptor for automatic trace ID injection.
- **ticho-trace-gateway**  
  Gateway-layer trace ID generation and propagation.
- **ticho-trace-okhttp**  
  OkHttp call chain propagation support.
- **ticho-trace-spring**  
  Spring MVC interceptor for request tracing.

---

## 🚀 Quick Start

### Add Starter Dependency

```xml
<!-- Example: Add Redis Starter -->
<dependency>
    <groupId>top.ticho.starter</groupId>
    <artifactId>ticho-starter-web</artifactId>
    <version>${ticho.boot.version}</version>
</dependency>