# Ticho-Boot 项目说明

Ticho-Boot 是一个高度模块化的 Java 基础开发框架，基于 Spring Boot 深度封装，提供企业级开发中常用的功能组件、工具类及标准化
Starter，助力快速搭建高可用微服务系统。

---

## 📂 项目结构概览

### 1. **ticho-dependencies**

- **项目依赖管理模块**  
  统一管理全局依赖版本，提供 Maven 父级 POM 配置，简化子模块版本控制。

---

### 2. **ticho-starter**

Spring Boot Starter 系列模块，开箱即用：

- **ticho-starter-cache**  
  缓存抽象层，支持本地缓存与分布式缓存（如 Redis）混合策略。
- **ticho-starter-cloud**  
  微服务核心组件，集成服务发现、配置中心等云原生能力。
- **ticho-starter-email**  
  邮件服务封装，支持模板化邮件发送。
- **ticho-starter-es**  
  ElasticSearch 快速集成，提供 DSL 构建器与高效查询工具。
- **ticho-starter-gateway**  
  动态网关支持，集成路由、限流、鉴权等 API 治理功能。
- **ticho-starter-health**  
  增强型健康检查，暴露应用状态与自定义探针接口。
- **ticho-starter-http**  
  feign 客户端增强，默认使用okhttp，支持负载均衡与自定义拦截器。
- **ticho-starter-log**  
  分布式日志追踪，集成 MDC 与日志染色功能。
- **ticho-starter-minio**  
  对象存储服务，支持 MinIO的S3协议的文件操作。
- **ticho-starter-rabbitmq**  
  RabbitMQ 消息队列增强。
- **ticho-starter-redis**  
  Redis简化配置、工具封装等。
- **ticho-starter-redisson**  
  分布式锁与并发控制，基于 Redisson 实现。
- **ticho-starter-security**  
  安全认证模块，支持 OAuth2/JWT 鉴权与动态权限管理。
- **ticho-starter-swagger**  
  API 文档自动化，集成 Swagger2、knife4j。
- **ticho-starter-view**  
  统一视图渲染。
- **ticho-starter-web**  
  Web 层增强，全局异常处理、参数校验与跨域配置。

---

### 3. **ticho-tool**

**通用工具库**，独立于框架的核心工具类：

- **ticho-tool-generator**  
  代码生成器，基于 Beetl 模板动态生成 CRUD 代码。
- **ticho-tool-intranet**  
  内网穿透工具，基于 Netty 实现 NAT 内网服务暴露。
- **ticho-tool-json**  
  Jackson实现，提供简化工具类。

---

### 4. **ticho-trace**

**全链路追踪体系**，兼容 OpenTracing 标准：

- **ticho-trace-common**  
  公共模型定义（Span、TraceID 等）。
- **ticho-trace-core**  
  链路上下文管理，支持日志关联与透传。
- **ticho-trace-feign**  
  Feign 客户端拦截器，自动注入追踪标识。
- **ticho-trace-gateway**  
  网关层流量标识生成与传播。
- **ticho-trace-okhttp**  
  OkHttp 调用链路透传支持。
- **ticho-trace-spring**  
  Spring MVC 拦截器，实现请求链路追踪。

---

## 🚀 快速开始

### 引入 Starter 依赖

```xml
<!-- 示例：引入 Redis Starter -->
<dependency>
    <groupId>top.ticho.starter</groupId>
    <artifactId>ticho-starter-redis</artifactId>
    <version>${ticho.boot.version}</version>
</dependency>