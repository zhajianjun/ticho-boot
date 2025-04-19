# Ticho-Boot

## 项目说明

Ticho-Boot 是一款由Spring Boot深度集成开发框架，通过模块化设计和标准化组件封装，显著提升Spring
Boot应用的快速构建能力。该框架基于前沿技术栈构建，提供开箱即用的功能组件与开发工具链，助力开发者聚焦核心业务逻辑实现。

**技术栈**

* 运行环境：JDK 17 LTS 长期支持版本
* 核心框架：Spring Boot 3.4.4（兼容Spring Framework 6.1.6）
* 分布式架构：Spring Cloud 2023.0.3 + Spring Cloud Alibaba 2023.0.3.2
* 安全体系：Spring Security 6.4.4 深度整合
* 持久层方案：Mybatis-Plus 3.5.11 + MySQL 8.0 驱动

**主要模块**

1. [ticho-dependencies](ticho-dependencies) 封装了全局依赖版本管理，提供Maven父级POM配置，简化子模块版本控制。
2. [ticho-tool](ticho-tool) 封装了一些常用的工具类，如代码生成、json序列化、内网穿透Netty等。
3. [ticho-starter](ticho-starter) 封装了Spring Boot相关功能组件、工具类及标准化Starter，使开发人员能够轻松地将这些特性集成到他们的应用程序。

---

## 📂 项目结构概览

### 1. **[ticho-dependencies](ticho-dependencies)**

- **项目依赖管理模块**  
  统一管理全局依赖版本，提供 Maven 父级 POM 配置，简化子模块版本控制。

---

### 2. **[ticho-starter](ticho-starter)**

Spring Boot Starter 系列模块，开箱即用：

- **[ticho-starter-cache](ticho-starter/ticho-starter-cache)**  
  缓存抽象层，支持本地缓存与分布式缓存（如 Redis）混合策略。
- **[ticho-starter-cloud](ticho-starter/ticho-starter-cloud)**  
  微服务核心组件，集成服务发现、配置中心等组件。
- **[ticho-starter-datasource](ticho-starter/ticho-starter-datasource)**  
  Mybatis Plus增强处理，定制化数据源、SQL日志追踪，增强批量插入、批量更新、插入更新等。
- **[ticho-starter-email](ticho-starter/ticho-starter-email)**  
  邮件服务封装，支持模板化邮件发送。
- **[ticho-starter-es](ticho-starter/ticho-starter-es)**  
  ElasticSearch 快速集成，提供DSL构建器与高效查询工具。
- **[ticho-starter-gateway](ticho-starter/ticho-starter-gateway)**  
  动态网关支持，集成路由、限流、鉴权等 API 治理功能。
- **[ticho-starter-health](ticho-starter/ticho-starter-health)**  
  增强型健康检查，暴露应用状态与自定义探针接口。
- **[ticho-starter-http](ticho-starter/ticho-starter-http)**  
  feign 客户端增强，默认使用okhttp，支持负载均衡与自定义拦截器。
- **[ticho-starter-log](ticho-starter/ticho-starter-log)**  
  分布式日志追踪，集成 MDC 与日志染色功能。
- **[ticho-starter-minio](ticho-starter/ticho-starter-minio)**  
  对象存储服务，支持 MinIO的S3协议的文件操作。
- **[ticho-starter-rabbitmq](ticho-starter/ticho-starter-rabbitmq)**
  RabbitMQ 消息队列增强。
- **[ticho-starter-redis](ticho-starter/ticho-starter-redis)**  
  Redis简化配置、工具封装等。
- **[ticho-starter-redisson](ticho-starter/ticho-starter-redisson)**
  分布式锁与并发控制，基于 Redisson 实现。
- **ticho-starter-security[ticho-starter-security](ticho-starter/ticho-starter-security)**  
  安全认证模块，支持 OAuth2/JWT 鉴权与动态权限管理。
- **[ticho-starter-view](ticho-starter/ticho-starter-view)ticho-starter-view**
  统一视图渲染。
- **[ticho-starter-web](ticho-starter/ticho-starter-web)**  
  Web 层增强，全局异常处理、参数校验与跨域配置。

---

### 3. **[ticho-tool](ticho-tool)**

**通用工具库**，独立于框架的核心工具类：

- **[ticho-tool-generator](ticho-tool/ticho-tool-generator)**  
  代码生成器，基于 Beetl 模板动态生成 CRUD 代码。
- **[ticho-tool-intranet](ticho-tool/ticho-tool-intranet)**  
  内网穿透工具，基于 Netty 实现 NAT 内网服务暴露。
- **[ticho-tool-json](ticho-tool/ticho-tool-json)**
  Jackson实现，提供简化工具类。

---

### 4. **ticho-trace**

**全链路追踪体系**

- **[ticho-trace-common](ticho-trace/ticho-trace-common)**
  公共模型定义（Span、TraceID 等）。
- **[ticho-trace-core](ticho-trace/ticho-trace-core)**    
  链路上下文管理，支持日志关联与透传。
- **[ticho-trace-feign](ticho-trace/ticho-trace-feign)**
  Feign 客户端拦截器，自动注入追踪标识。
- **[ticho-trace-gateway](ticho-trace/ticho-trace-gateway)**
  网关层流量标识生成与传播。
- **[ticho-trace-okhttp](ticho-trace/ticho-trace-okhttp)**   
  OkHttp 调用链路透传支持。
- **[ticho-trace-spring](ticho-trace/ticho-trace-spring)**   
  Spring MVC 拦截器，实现请求链路追踪。

---

## 🚀 快速开始

### 引入 Starter 依赖

```xml
<!-- 示例：引入 Redis Starter -->
<dependency>
    <groupId>top.ticho.starter</groupId>
    <artifactId>ticho-starter-web</artifactId>
    <version>${ticho.boot.version}</version>
</dependency>
```

---

## 🎗 许可证

本项目采用 [MIT 许可证](https://choosealicense.com/licenses/mit/)
，更多细节请参阅 [LICENSE](https://github.com/zhajianjun/ticho-boot/blob/main/LICENSE) 文件。