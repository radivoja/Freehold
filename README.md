# Freehold

Spring Boot / Spring Cloud microservices project demonstrating service discovery, inter-service communication, and AWS integrations, all running locally with Docker Compose and LocalStack.

## Architecture

```
                        ┌──────────────┐
                        │   Customer   │ :8092  (Kotlin, Thymeleaf UI)
                        │   Service    │
                        └──────┬───────┘
                               │ OpenFeign
                               ▼
┌──────────────┐       ┌──────────────┐        ┌──────────────┐
│   Eureka     │◄──────│    Market    │  SQS   │  Production  │
│   Register   │ :8761 │   Service    │◄──────►│   Service    │
└──────────────┘       │              │ :8091  │              │ :8090
                       └──────┬───────┘        └──────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
        ┌──────────┐   ┌──────────┐    ┌──────────┐
        │ DynamoDB │   │    S3    │    │   SNS    │
        │  Audit   │   │ Invoices │    │  Notify  │
        └──────────┘   └──────────┘    └──────────┘
                    LocalStack :4566
```

## Services

| Service    | Port | Language | Description                                            |
|------------|------|----------|--------------------------------------------------------|
| register   | 8761 | Java     | Eureka service discovery server                        |
| production | 8090 | Java     | Manages raw goods inventory, fulfills supply orders    |
| market     | 8091 | Java     | Marketplace for finished goods, async ordering via SQS |
| customer   | 8092 | Kotlin   | Web UI (Thymeleaf) for browsing and purchasing goods   |

## Tech Stack

- **Java 17** / **Kotlin** (customer service)
- **Spring Boot 3.2.3** / **Spring Cloud 2023.0.0**
- **Spring Cloud Netflix Eureka** for service discovery
- **Spring Cloud OpenFeign** for declarative REST clients
- **H2** embedded databases
- **Lombok** for boilerplate reduction
- **ModelMapper** for DTO mapping
- **AWS SDK v2** (2.25.0) with LocalStack emulation
- **Docker** / **Docker Compose** for orchestration

## AWS Integrations

All AWS services run locally via **LocalStack** (no AWS account needed).

| Service  | Usage                                                                |
|----------|----------------------------------------------------------------------|
| SQS      | Async messaging between market and production (two queues)           |
| DynamoDB | Audit log with composite key (service#timestamp) tracking all events |
| S3       | Invoice storage (JSON documents in `freehold-invoices` bucket)       |
| SNS      | Order status notifications (topic: `order-notifications`)            |

### SQS Message Flow

1. Market publishes a supply order to `production-orders` queue
2. Production picks it up, checks stock, and fulfills or rejects
3. Production publishes result to `production-results` queue
4. Market consumes the result, stocks goods (if fulfilled), generates invoice, logs audit event, and sends notification

## Prerequisites

- **Docker** and **Docker Compose**
- No local Java, Maven, or AWS CLI needed (everything builds and runs inside containers)

## Getting Started

```bash
# Clone the repository
git clone https://github.com/radivoja/Freehold.git
cd Freehold

# Build and start all services
docker compose up -d --build

# Verify all services are running
docker compose ps
```

Wait about 30 seconds for all services to register with Eureka.

## API Endpoints

### Market Service (port 8091)

```
GET    /market                          List all market goods
GET    /market/{type}                   Get specific goods by type
PUT    /market/sell/{type}              Sell one unit of a goods type
GET    /shipments                       List all shipments
PUT    /shipments/async/{type}/{amount} Queue async supply order
```

### Production Service (port 8090)

```
GET    /goods                           List all production goods
```

### Customer Service (port 8092)

```
GET    /index                           Home page
GET    /list                            Browse available goods
GET    /purchase?type=X                 Purchase goods by type
```

### Goods Types

`FOOD`, `WOOD`, `STONE`, `COPPER`, `IRON`, `GOLD`

## Example: Async Supply Order

```bash
# Queue a supply order for 10 units of WOOD
curl -X PUT http://localhost:8091/shipments/async/WOOD/10

# Response:
# {"orderId":"...","status":"QUEUED","message":"Supply order queued for processing"}

# Check market stock after processing (~5 seconds)
curl http://localhost:8091/market/WOOD
```

## Admin UIs

| UI               | Port | Description                        |
|------------------|------|------------------------------------|
| Eureka Dashboard | 8761 | Service registry and health status |
| DynamoDB Admin   | 8001 | Browse audit log table             |
| S3 Browser       | 8002 | Browse invoice files in S3 buckets |

## Project Structure

```
Freehold/
├── Dockerfile                  # Multi-stage build (Maven + JRE)
├── docker-compose.yml          # Full stack orchestration
├── localstack/
│   └── 01-init-aws.sh          # Creates SQS queues, DynamoDB table, S3 bucket, SNS topic
├── register/                   # Eureka server
├── production/                 # Production service (Java)
│   └── src/main/java/freehold/production/
│       ├── config/             # AWS client configuration
│       ├── controller/         # REST endpoints
│       ├── messaging/          # SQS publishers and listeners
│       ├── model/              # Entities, DTOs, enums
│       ├── repository/         # JPA repositories
│       └── services/           # Business logic
├── market/                     # Market service (Java)
│   └── src/main/java/freehold/market/
│       ├── audit/              # DynamoDB audit logging
│       ├── client/             # Feign clients
│       ├── config/             # AWS client configuration
│       ├── controller/         # REST endpoints
│       ├── invoice/            # S3 invoice generation
│       ├── messaging/          # SQS publishers and listeners
│       ├── model/              # Entities, DTOs, enums
│       ├── notification/       # SNS order notifications
│       ├── repository/         # JPA repositories
│       └── services/           # Business logic
└── customer/                   # Customer service (Kotlin)
    └── src/main/kotlin/freehold/customer/
        ├── client/             # Feign client to market
        ├── controller/         # Thymeleaf MVC controller
        └── model/              # DTOs and enums
```

## Docker

Each service uses the same shared `Dockerfile` with a build argument:

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
ARG SERVICE
WORKDIR /build
COPY ${SERVICE}/pom.xml .
COPY ${SERVICE}/src ./src
RUN mvn -DskipTests -q package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Environment variables configure Eureka and LocalStack endpoints per service in `docker-compose.yml`.

## Stopping

```bash
docker compose down
```
