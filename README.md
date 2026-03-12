# Freehold

A Spring Boot microservices project for managing Freehold business domains through independent services for production, market, and customer operations.

## Overview

Freehold is a multi-module microservices-based application built with Spring Boot and Spring Cloud.  
The system is organized into separate services that communicate through service discovery using Eureka.

The repository contains the following modules:

- **register** – Eureka service registry
- **production** – production-related service
- **market** – market-related service
- **customer** – customer-facing service

## Tech Stack

- Java 17
- Kotlin
- Spring Boot 3
- Spring Cloud
- Netflix Eureka
- OpenFeign
- Spring Web
- Spring Data JPA
- H2 Database
- Thymeleaf
- Actuator
- Maven
- Lombok
- ModelMapper

## Project Modules

### register
Service registry module used for Eureka-based service discovery.

- Spring Boot service
- Eureka Server
- Runs on port **8761**

### production
Production service responsible for production-related business logic.

- Spring Web
- Spring Data JPA
- H2 database
- Eureka Client
- OpenFeign
- Actuator
- Runs on port **8090**

### market
Market service responsible for market-related business logic.

- Spring Web
- Spring Data JPA
- H2 database
- Eureka Client
- OpenFeign
- Actuator
- ModelMapper
- Runs on port **8091**

### customer
Customer service implemented with Kotlin.

- Spring Web
- Thymeleaf
- Eureka Client
- OpenFeign
- Kotlin support
- Runs on port **8092**

## Architecture

The project follows a microservices architecture where each domain is separated into its own service.

- **Service discovery** is handled by the `register` module using Eureka
- `production`, `market`, and `customer` register themselves with Eureka
- Services can communicate using **OpenFeign**
- `market` and `production` persist data using local **H2 file databases**

## Default Ports

- `register` → **8761**
- `production` → **8090**
- `market` → **8091**
- `customer` → **8092**

## Prerequisites

Before running the project, make sure you have:

- Java 17
- Maven 3+
- Git

## Running the Project

Start the services in this order:

### 1. Run Eureka Registry

```bash
cd register
mvn spring-boot:run
### Additional Links
These additional references should also help you:

* [Declarative REST calls with Spring Cloud OpenFeign sample](https://github.com/spring-cloud-samples/feign-eureka)

