# Architecture

This document describes the architecture, implementation decisions, and trade-offs made for the Music Metadata Service.

---

# Overview

The application is a Spring Boot REST service backed by PostgreSQL for managing artist and track metadata.

The implementation focuses on maintainability, clear separation of responsibilities, database consistency, automated testing, and production-oriented practices.

---

# Application Structure

The project follows a **package-by-feature** structure.

```text
com.deepak.music
├── artist/
├── track/
├── artistoftheday/
├── common/
│   ├── config/
│   ├── exception/
│   ├── security/
│   └── web/
└── MusicMetadataServiceApplication.java
```

Each feature contains its related controller, service, repository, DTO, mapper and validation logic.

This keeps business capabilities cohesive and avoids unnecessary coupling between technical layers.

---

# Security Design

The application uses stateless JWT authentication with Role-Based Access Control.

## Authentication Flow

1. Client authenticates using `/api/v1/auth/login`.
2. The service validates credentials and returns a JWT token.
3. The client includes the token in subsequent requests.

```http
Authorization: Bearer <jwt-token>
```

## Authorisation Model

* Read operations are publicly accessible.
* Write operations require the `ADMIN` role.
* Security rules are enforced using Spring Security configuration and method-level security.

Authentication is implemented within the application rather than as a separate authentication service because a dedicated service would add unnecessary complexity for this exercise.

---

# Data Model Design

## Artist

The Artist entity contains:

* UUID identifier
* Artist name
* Creation and update timestamps
* Optimistic locking version

## Track

The Track entity contains:

* UUID identifier
* Associated artist reference
* Title
* Genre
* Duration
* Creation and update timestamps
* Optimistic locking version

---

# Database Design Decisions

## UUID Primary Keys

UUIDs are used to avoid exposing sequential identifiers and allow identifier generation independently of the database.

---

## Flyway Migrations

Database schema changes are managed through versioned Flyway migrations.

Hibernate schema generation is disabled and configured only for validation to ensure database changes are explicit and controlled.

---

## Optimistic Locking

Mutable entities use optimistic locking through a version column.

Concurrent updates with stale versions are rejected rather than silently overwriting changes.

---

## Genre Validation

Genres are represented as Java enums and stored as strings.

A database constraint ensures that only supported values are persisted.

---

# API Error Handling

The application uses Spring `ProblemDetail` responses through centralized exception handling.

This provides a consistent error contract across all REST endpoints.

---

# Artist of the Day Algorithm

The featured artist is calculated deterministically using the current UTC day.

```text
offset = floorMod(epochDayUtc, totalArtists)
```

Artists are ordered by:

```text
created_at, id
```

This guarantees:

* Fair rotation across all artists.
* Consistent results across application restarts.
* No dependency on scheduled jobs.
* Safe behaviour across multiple application instances.

---

# Testing Strategy

The project uses a combination of unit and integration testing.

- Unit tests validate business logic independently.
- Integration tests verify API, persistence, and database behaviour.

Testcontainers is used for integration tests to run against a real PostgreSQL database rather than mocks or an in-memory database.

This provides higher confidence that database interactions behave correctly in a production-like environment.

---

# Observability and Operations

The application includes operational features to support production deployment.

## Health Checks

Spring Boot Actuator provides health and operational endpoints used for monitoring application status.

## Request Tracing

Each request supports an `X-Correlation-Id` header to simplify tracing across application logs.

---

# Future Enhancements

Potential future improvements:

* Artist alias history and search
* Track update/delete operations
* Soft delete support
* Dedicated authentication service
* Redis caching
* Full-text search
* Rate limiting
* Audit logging
* Distributed tracing
