# Refactor Microservice — Clean Package Architecture Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reorganize all Java classes in `order-service`, `inventory-service`, and `api-gateway` from flat single-package layout into clean layered sub-packages, mirroring the reference repo structure.

**Architecture:** Each Spring Boot microservice gets sub-packages for `controller`, `service`, `repository`, `entity`, `dto/request`, `dto/response`, `event`, `kafka`, `exception`, and `config`. Logic is unchanged — only package declarations and import statements move. No new dependencies are added.

**Tech Stack:** Java 17, Spring Boot 4.1.0, Spring Cloud 2025.1.2, Kafka, PostgreSQL, Gradle, Lombok, Eureka, OpenFeign

**Spec:** This plan derives from the reference repo at `C:\Users\adminlk\.gemini\antigravity\scratch\reference-repo` and the source repo at `C:\Users\adminlk\.gemini\antigravity\scratch\source-repo`.

## Global Constraints

- Keep existing build.gradle, settings.gradle, application.yml, docker-compose.yml files unchanged.
- Do NOT change any business logic inside method bodies.
- Preserve all Lombok annotations (`@Data`, `@RequiredArgsConstructor`, etc.).
- All new package names must stay under the existing root package (`ra.orderservice`, `ra.inventoryservice`, `ra.apigateway`).
- Each service's `Application` class stays in the root package (no sub-package).
- `eureka-server` has no Java files to refactor — skip it.

## Review Focus

- Import statements in `OrderService.java` must correctly reference moved classes (ProductClient, OrderRepository, OrderProducer, Order, OrderRequest, ProductResponse).
- `ProductConsumer.java` import of `OrderEvent` must still resolve after both are moved to `kafka/` and `event/`.
- `ProductClient.java` Feign annotation and `ProductResponse.java` must compile together after split to `client/` and `dto/response/`.
- `GlobalLoggingFilter.java` moved to `filter/` must still be scanned by Spring component scan from root package.
- Application classes must remain in root package so `@SpringBootApplication` component scan covers all sub-packages.

---

## Task 1: Refactor `order-service` — create sub-packages and move files

**Files:**
- Create: `order-service/src/main/java/ra/orderservice/entity/Order.java`
- Create: `order-service/src/main/java/ra/orderservice/controller/OrderController.java`
- Create: `order-service/src/main/java/ra/orderservice/service/OrderService.java`
- Create: `order-service/src/main/java/ra/orderservice/repository/OrderRepository.java`
- Create: `order-service/src/main/java/ra/orderservice/dto/request/OrderRequest.java`
- Create: `order-service/src/main/java/ra/orderservice/event/OrderEvent.java`
- Create: `order-service/src/main/java/ra/orderservice/kafka/OrderProducer.java`
- Create: `order-service/src/main/java/ra/orderservice/client/ProductClient.java`
- Create: `order-service/src/main/java/ra/orderservice/dto/response/ProductResponse.java`
- Delete: all flat files in `order-service/src/main/java/ra/orderservice/` except `OrderServiceApplication.java`

**Interfaces:**
- Produces: All order-service classes in layered packages, with updated `package` declarations and `import` statements.

- [ ] **Step 1: Create `entity/Order.java`**

  Copy `Order.java`, change package to `ra.orderservice.entity`. No imports change.

- [ ] **Step 2: Create `dto/request/OrderRequest.java`**

  Copy `OrderRequest.java`, change package to `ra.orderservice.dto.request`. No imports change.

- [ ] **Step 3: Create `event/OrderEvent.java`**

  Copy `OrderEvent.java`, change package to `ra.orderservice.event`. No imports change.

- [ ] **Step 4: Create `dto/response/ProductResponse.java`**

  Copy `ProductResponse.java`, change package to `ra.orderservice.dto.response`. No imports change.

- [ ] **Step 5: Create `repository/OrderRepository.java`**

  Copy `OrderRepository.java`, change package to `ra.orderservice.repository`. Add import `ra.orderservice.entity.Order`.

- [ ] **Step 6: Create `client/ProductClient.java`**

  Copy `ProductClient.java`, change package to `ra.orderservice.client`. Add import `ra.orderservice.dto.response.ProductResponse`.

- [ ] **Step 7: Create `kafka/OrderProducer.java`**

  Copy `OrderProducer.java`, change package to `ra.orderservice.kafka`. Add import `ra.orderservice.event.OrderEvent`.

- [ ] **Step 8: Create `service/OrderService.java`**

  Copy `OrderService.java`, change package to `ra.orderservice.service`. Add imports:
  ```
  ra.orderservice.client.ProductClient
  ra.orderservice.repository.OrderRepository
  ra.orderservice.kafka.OrderProducer
  ra.orderservice.entity.Order
  ra.orderservice.dto.request.OrderRequest
  ra.orderservice.dto.response.ProductResponse
  ```

- [ ] **Step 9: Create `controller/OrderController.java`**

  Copy `OrderController.java`, change package to `ra.orderservice.controller`. Add imports:
  ```
  ra.orderservice.service.OrderService
  ra.orderservice.entity.Order
  ra.orderservice.dto.request.OrderRequest
  ```

- [ ] **Step 10: Delete old flat files**

  Delete from `ra/orderservice/` root (keep `OrderServiceApplication.java`):
  `Order.java`, `OrderController.java`, `OrderService.java`, `OrderRepository.java`,
  `OrderRequest.java`, `OrderEvent.java`, `OrderProducer.java`,
  `ProductClient.java`, `ProductResponse.java`

- [ ] **Step 11: Commit**

  ```bash
  git add order-service/src/main/java/ra/orderservice/
  git commit -m "refactor(order-service): reorganize into clean sub-packages"
  ```

---

## Task 2: Refactor `inventory-service` — create sub-packages and move files

**Files:**
- Create: `inventory-service/src/main/java/ra/inventoryservice/entity/Product.java`
- Create: `inventory-service/src/main/java/ra/inventoryservice/repository/ProductRepository.java`
- Create: `inventory-service/src/main/java/ra/inventoryservice/service/ProductService.java`
- Create: `inventory-service/src/main/java/ra/inventoryservice/controller/ProductController.java`
- Create: `inventory-service/src/main/java/ra/inventoryservice/event/OrderEvent.java`
- Create: `inventory-service/src/main/java/ra/inventoryservice/kafka/ProductConsumer.java`
- Delete: all flat files in `ra/inventoryservice/` except `InventoryServiceApplication.java`

**Interfaces:**
- Produces: All inventory-service classes in layered packages.

- [ ] **Step 1: Create `entity/Product.java`**

  Copy `Product.java`, change package to `ra.inventoryservice.entity`. No imports change.

- [ ] **Step 2: Create `event/OrderEvent.java`**

  Copy `OrderEvent.java`, change package to `ra.inventoryservice.event`. No imports change.

- [ ] **Step 3: Create `repository/ProductRepository.java`**

  Copy `ProductRepository.java`, change package to `ra.inventoryservice.repository`. Add import `ra.inventoryservice.entity.Product`.

- [ ] **Step 4: Create `service/ProductService.java`**

  Copy `ProductService.java`, change package to `ra.inventoryservice.service`. Add imports:
  ```
  ra.inventoryservice.entity.Product
  ra.inventoryservice.repository.ProductRepository
  ```

- [ ] **Step 5: Create `controller/ProductController.java`**

  Copy `ProductController.java`, change package to `ra.inventoryservice.controller`. Add imports:
  ```
  ra.inventoryservice.entity.Product
  ra.inventoryservice.service.ProductService
  ```

- [ ] **Step 6: Create `kafka/ProductConsumer.java`**

  Copy `ProductConsumer.java`, change package to `ra.inventoryservice.kafka`. Add imports:
  ```
  ra.inventoryservice.event.OrderEvent
  ra.inventoryservice.entity.Product
  ra.inventoryservice.repository.ProductRepository
  ```

- [ ] **Step 7: Delete old flat files**

  Delete from `ra/inventoryservice/` root (keep `InventoryServiceApplication.java`):
  `Product.java`, `ProductController.java`, `ProductService.java`, `ProductRepository.java`,
  `OrderEvent.java`, `ProductConsumer.java`

- [ ] **Step 8: Commit**

  ```bash
  git add inventory-service/src/main/java/ra/inventoryservice/
  git commit -m "refactor(inventory-service): reorganize into clean sub-packages"
  ```

---

## Task 3: Refactor `api-gateway` — create `filter/` sub-package

**Files:**
- Create: `api-gateway/src/main/java/ra/apigateway/filter/GlobalLoggingFilter.java`
- Delete: `api-gateway/src/main/java/ra/apigateway/GlobalLoggingFilter.java`

**Interfaces:**
- Produces: `GlobalLoggingFilter` in `ra.apigateway.filter` sub-package.

- [ ] **Step 1: Create `filter/GlobalLoggingFilter.java`**

  Copy `GlobalLoggingFilter.java`, change package to `ra.apigateway.filter`. No import changes needed.

- [ ] **Step 2: Delete old file**

  Delete `api-gateway/src/main/java/ra/apigateway/GlobalLoggingFilter.java`.

- [ ] **Step 3: Commit**

  ```bash
  git add api-gateway/src/main/java/ra/apigateway/
  git commit -m "refactor(api-gateway): move GlobalLoggingFilter to filter sub-package"
  ```
