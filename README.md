# 🛒 E-Commerce Microservices System (Spring Boot + Kafka + Docker)

This project is a **microservices-based e-commerce backend system** built using **Spring Boot**, **Spring Cloud**, **Kafka**, and **Docker**.  
It demonstrates a real-world architecture with **Order Management, Inventory Management, and Notification Service** communicating asynchronously via Kafka events.

---

## 📌 Architecture Overview

The system is composed of the following services:

- **Order Service** → Handles order creation, payment, shipping, and status changes
- **Inventory Service** → Manages stock reservation, confirmation, and release
- **Notification Service** → Sends emails and stores notifications based on events
- **Common Events Module** → Shared DTOs/events used across services
- **Kafka Broker** → Event-driven communication between services
- **MySQL Databases** → Separate database per microservice

---

## ⚙️ Tech Stack

- Java 17+
- Spring Boot
- Spring Cloud (Feign Clients)
- Spring Data JPA
- Apache Kafka
- MySQL
- Docker & Docker Compose
- Lombok
- JavaMailSender (SMTP Gmail)
- Eureka (Service Discovery)

---

## 📡 Event-Driven Communication

Kafka is used for asynchronous communication between services.

### Topics:
- `order-events`
- `inventory-events`
- `notification-events`

### Event Flow Example:
1. Order is created in **Order Service**
2. Inventory is reserved via REST (Feign)
3. Order event is published to Kafka
4. **Notification Service** consumes event
5. Email + DB notification is created

---

## 📦 Common Events Module

Shared library used across services:

### Event Types:
```java
ORDER_CREATED,
ORDER_RESERVED,
ORDER_PAID,
ORDER_SHIPPED,
ORDER_DELIVERED,
ORDER_CANCELLED

```

---

##
### Each service runs on:

```java
Service	Port
Order Service	8081
Inventory Service	8082
Notification Service	8084
Eureka Server	8761