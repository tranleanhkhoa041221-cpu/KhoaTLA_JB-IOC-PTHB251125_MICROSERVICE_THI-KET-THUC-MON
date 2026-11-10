# 🛒 Order Management Microservices System
> Java Spring Boot · Spring Cloud · Kafka · PostgreSQL · Docker

A microservices-based order management system demonstrating the **dual communication pattern**: synchronous service-to-service calls via **OpenFeign** and asynchronous event-driven stock deduction via **Apache Kafka**.

---

## 📋 Table of Contents
- [Architecture](#-architecture)
- [Services](#-services)
- [Tech Stack](#-tech-stack)
- [Communication Patterns](#-communication-patterns)
- [Getting Started](#-getting-started)
- [API Overview](#-api-overview)

---

## 🏗️ Architecture

```
                    ┌─────────────────┐
                    │  Eureka Server  │  :8761 — Service Registry
                    │  (Netflix OSS)  │
                    └────────┬────────┘
                             │ (register & discover)
              ┌──────────────┼──────────────┐
              ↓              ↓              ↓
     ┌────────────────┐  ┌──────────────┐  ┌──────────────────┐
     │  API Gateway   │  │ Order Service│  │Inventory Service │
     │   :8080        │  │   :8081      │  │    :8082         │
     │Spring Cloud GW │  │              │  │                  │
     └───────┬────────┘  └──────┬───────┘  └──────────────────┘
             │                  │  [Feign - sync]       ↑
         [routes]               └──────────────────────→│ stock check
             ├──→ /api/orders/**   → order-service       │
             └──→ /api/inventory/**→ inventory-service   │
                                       │                 │
                                  [Kafka async]          │
                                       └─── order-topic ─┘
                                                ↑
                                     [Apache Kafka :9092]
                                       (KRaft mode)
                                                ↑
                                     [PostgreSQL :5433]
                                   order_db | inventory_db
```

---

## 🧩 Services

| Service | Port | Description |
|---|---|---|
| **eureka-server** | 8761 | Netflix Eureka — Service Registry. All services register here. |
| **api-gateway** | 8080 | Spring Cloud Gateway (WebMVC). Routes traffic to downstream services using Eureka load-balanced URIs (`lb://`). |
| **order-service** | 8081 | Handles order creation. Validates stock synchronously via Feign, saves order as `PENDING`, then publishes Kafka event to deduct stock. |
| **inventory-service** | 8082 | Manages product/inventory data. Exposes REST for stock queries. Consumes Kafka `order-topic` to deduct stock asynchronously with pessimistic locking. |

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 4.1.0 |
| **Build Tool** | Gradle (Wrapper) |
| **Service Registry** | Spring Cloud Netflix Eureka (Spring Cloud 2025.1.2) |
| **API Gateway** | Spring Cloud Gateway Server WebMVC |
| **Service Communication (sync)** | Spring Cloud OpenFeign (`@FeignClient`) |
| **Message Broker (async)** | Apache Kafka 4.1.2 (KRaft mode — no ZooKeeper) |
| **Database** | PostgreSQL (separate DB per service) |
| **ORM** | Spring Data JPA + Hibernate |
| **Containerization** | Docker Compose (Kafka) |

---

## 🔄 Communication Patterns

### Synchronous — OpenFeign
```java
@FeignClient(name = "inventory-service")
public interface ProductClient {
    @GetMapping("/api/inventory/{productId}")
    ProductResponse getProduct(@PathVariable Long productId);
}
```
- Order Service → Inventory Service to validate stock **before** saving order
- Returns HTTP 409 Conflict if stock is insufficient

### Asynchronous — Apache Kafka
```
Order Service (Producer)  →  "order-topic"  →  Inventory Service (Consumer)
```
- After order is saved as `PENDING`, Kafka event fires to deduct stock
- Consumer uses **pessimistic locking** (`findByIdWithLock`) to prevent race conditions

### Complete Order Flow
```
1. POST /api/orders?productId=X&quantity=Y
2. Order Service → [Feign] → Inventory Service: check stock
   → 409 if insufficient
3. Order Service: save Order (status: PENDING) to order_db
4. Order Service → [Kafka] → "order-topic" → Inventory Service: deduct stock
5. Inventory Service: UPDATE product SET quantity = quantity - N (pessimistic lock)
```

### Why Both Feign AND Kafka?

| | OpenFeign (sync) | Kafka (async) |
|---|---|---|
| **Purpose** | Immediate stock validation — client gets instant result | Decouple stock deduction — resilient, no blocking |
| **If service down** | Returns error immediately | Event retained; processed when service recovers |
| **Client experience** | Knows right away if order is possible | No impact on response time |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 15+ (running locally on port 5433)
- Docker & Docker Compose

### 1. Start Kafka
```bash
docker-compose up -d
# Starts Apache Kafka (KRaft mode) on port 9092
```

### 2. Create PostgreSQL Databases
```sql
CREATE DATABASE order_db;
CREATE DATABASE inventory_db;
```

### 3. Start Services (in order)
```bash
# Terminal 1 — Eureka Server (start first)
cd eureka-server && ./gradlew bootRun

# Terminal 2 — Inventory Service
cd inventory-service && ./gradlew bootRun

# Terminal 3 — Order Service
cd order-service && ./gradlew bootRun

# Terminal 4 — API Gateway (start last)
cd api-gateway && ./gradlew bootRun
```

### Service URLs
| Service | URL |
|---|---|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8080 |
| Order Service (direct) | http://localhost:8081 |
| Inventory Service (direct) | http://localhost:8082 |

---

## 🌐 API Overview

All requests go through the **API Gateway** at `http://localhost:8080`

### Inventory
```http
GET    /api/inventory              # List all products
GET    /api/inventory/{id}         # Get product by ID
POST   /api/inventory              # Create product { "productName": "Laptop", "quantity": 10, "price": 1500 }
PUT    /api/inventory/{id}         # Update product
```

### Orders
```http
POST   /api/orders?productId={id}&quantity={n}  # Place an order
GET    /api/orders                 # List all orders
GET    /api/orders/{id}            # Get order by ID
```

#### Sample Responses
**Success (200):**
```json
{ "id": 1, "productId": 1, "quantity": 2, "status": "PENDING", "totalPrice": 3000 }
```
**Out of stock (409):**
```json
{ "status": 409, "error": "Conflict", "message": "Insufficient stock" }
```

---

## 👤 Author

**Trần Lê Anh Khoa** — Backend Developer (Java Spring Boot)  
📧 tranleanhkhoa041221@gmail.com  
🔗 [GitHub](https://github.com/tranleanhkhoa041221-cpu)
