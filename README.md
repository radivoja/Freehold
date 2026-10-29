# Freehold

Serbian artisan food & spirits marketplace built with microservices architecture, running locally with Docker Compose and MiniStack (AWS emulation).

## Architecture

```
                        Browser
                           |
                    Cognito (login)
                           |
                      SPA (React)
                           |
                  API Gateway (JWT Authorizer)
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

In production the SPA is a static bundle on S3 served by CloudFront. API Gateway validates JWT tokens at the edge and routes requests to the backend services. Locally, nginx proxies API calls from the SPA to backend containers.

## Services

| Service | Language | Port | Description |
|---------|----------|------|-------------|
| Production | Java / Spring Boot | 8090 | Goods inventory with Datafaker seeder |
| Marketplace | Java / Spring Boot | 8091 | Market prices, stock levels, SQS-driven supply |
| Order | Kotlin / Spring Boot | 8093 | Order placement and lifecycle management |
| Notification | Python / FastAPI | 8092 | Event-driven alerts via SQS consumer |
| SPA | React / Vite | 3000 | Frontend application |

## Tech Stack

**Backend:** Spring Boot 3.2.3, Kotlin 1.9.22, Python 3.11, FastAPI

**Frontend:** React 18, Vite 5, React Router 6

**Database:** PostgreSQL 16 (ddl-auto=update)

**AWS (emulated via MiniStack):**
- Cognito (user authentication, JWT tokens)
- API Gateway v2 (HTTP proxy routing, JWT authorizer)
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
| http://localhost:5050 | pgAdmin (admin@freehold.com/admin) |

## SPA Development

For hot-reload frontend development without Docker:

```bash
cd spa
npm install
npm run dev
```

Vite proxies API calls to backend services on their local ports.

## Authentication

Dual-layer JWT authentication using AWS Cognito:

1. **API Gateway JWT Authorizer** — validates token at the edge (signature, expiry, audience). Protects production routes. Rejects bad requests with 401 before they hit services.
2. **Spring Boot OAuth2 Resource Server** — defense in depth. Each backend validates the JWT again and extracts user claims.

**Test credentials:** `test@freehold.com` / `Test1234!`

Login is required — unauthenticated users see only the login page. The SPA uses `amazon-cognito-identity-js` for authentication and attaches the JWT as a Bearer token on every API call.

| Route | Auth |
|-------|------|
| GET /goods | Public (browse without login) |
| PUT /goods/** | Authenticated (JWT required) |
| /marketplace/** | Public |
| /order/** | Public |
| /notification/** | Public |

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
