# Freehold

Serbian artisan food & spirits marketplace built with microservices architecture, running locally with Docker Compose and MiniStack (AWS emulation).

## Architecture

```
                        Browser
                           |
                      SPA (React)
                           |
                     API Gateway
                           |
       +------------+------+------+-------------+
       |            |             |              |
  Production   Marketplace     Order       Notification
   (Java)       (Java)       (Kotlin)     (Python/FastAPI)
       |            |             |
       +------------+------+------+
                           |
                      PostgreSQL
```

In production the SPA is a static bundle on S3 served by CloudFront. API Gateway routes requests to the backend services. Locally, nginx proxies API calls from the SPA to backend containers.

## Services

| Service | Language | Port | Description |
|---------|----------|------|-------------|
| Production | Java / Spring Boot | 8090 | Goods inventory with 10M row Datafaker seeder |
| Marketplace | Java / Spring Boot | 8091 | Market prices, stock levels, SQS-driven supply |
| Order | Kotlin / Spring Boot | 8093 | Order placement and lifecycle management |
| Notification | Python / FastAPI | 8092 | Event-driven alerts via SQS consumer |
| SPA | React / Vite | 3000 | Frontend application |

## Tech Stack

**Backend:** Spring Boot 3.2.3, Kotlin 1.9.22, Python 3.11, FastAPI

**Frontend:** React 18, Vite 5, React Router 6

**Database:** PostgreSQL 16 (ddl-auto=update)

**AWS (emulated via MiniStack):**
- API Gateway v2 (HTTP proxy routing)
- SQS (event queues between services)
- SNS (notification topics)
- S3 + CloudFront (SPA static hosting)
- DynamoDB (audit logging)
- CloudWatch (metrics)

**Monitoring:** Grafana 10.4 with CloudWatch datasource

**Infrastructure:** Docker Compose, nginx, Maven

## Prerequisites

- Docker Desktop

No local Java, Kotlin, Maven, Node.js or AWS CLI needed. Everything builds and runs inside containers.

## Running

```bash
docker compose up --build
```

| URL | What |
|-----|------|
| http://localhost:3000 | SPA |
| http://localhost:8090 | Production API |
| http://localhost:8091 | Marketplace API |
| http://localhost:8092 | Notification API |
| http://localhost:8093 | Order API |
| http://localhost:4566 | MiniStack (AWS emulator) |
| http://localhost:3001 | Grafana (admin/admin) |

## SPA Development

For hot-reload frontend development without Docker:

```bash
cd spa
npm install
npm run dev
```

Vite proxies API calls to backend services on their local ports.

## API Endpoints

### Production (:8090)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /goods | List all goods |
| GET | /goods/{id} | Get goods by ID |
| PUT | /goods/{id}/{amount} | Update quantity |

### Marketplace (:8091)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /market | List all market items |
| GET | /market/{type} | Get by subcategory |
| PUT | /market/sell/{type} | Sell item |

### Order (:8093)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /orders | Place an order |
| GET | /orders | List all orders |
| GET | /orders/{id} | Get order by ID |
| GET | /orders/customer/{customerId} | Get by customer |
| PATCH | /orders/{id}/status?status=X | Update status |

### Notification (:8092)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /notifications | List all notifications |
| GET | /notifications/count | Get count |
| GET | /health | Health check |

## Order Lifecycle

```
PLACED -> CONFIRMED -> SHIPPED -> DELIVERED
                               -> CANCELLED
```

## Project Structure

```
Freehold/
├── production/          Java/Maven - Goods inventory service
├── marketplace/         Java/Maven - Marketplace service
├── order/               Kotlin/Maven - Order service
├── notification/        Python/FastAPI - Notification service
├── spa/                 React/Vite - Frontend
├── ministack/           AWS init scripts (API Gateway, SQS, SNS, S3, CloudFront)
├── postgres/            Database init SQL
├── grafana/             Dashboards and provisioning
└── docker-compose.yml   Service orchestration
```

## Stopping

```bash
docker compose down
```
