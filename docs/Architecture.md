# Development Guide

This document describes the implementation approach, architecture, design decisions, security model, testing strategy, and operational considerations for the Music Metadata Service.

---

# Overview

The application is a REST API built with Spring Boot and PostgreSQL for managing artist and track metadata. The implementation emphasises clean architecture, maintainability, database versioning, automated testing, and production-oriented development practices.

---

# Assumptions

The original requirements intentionally leave some implementation details open. The following assumptions were made:

* Each track belongs to a single artist.
* Artist names are editable while artist identifiers remain immutable.
* Tracks cannot exist without an associated artist.
* Artist deletion is outside the scope of this exercise.
* The Artist of the Day rotates fairly across all artists using a deterministic algorithm.
* Authentication and role-based authorisation were added as a production-oriented enhancement, although not explicitly required by the specification.

---

# Architecture

The project follows a **package-by-feature** structure to keep related components together and reduce coupling between features.

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

Each feature contains its own controller, service, repository, DTOs, mapper and validation logic, making the application easier to maintain and extend.

---

# Security

Spring Security is configured using stateless JWT bearer authentication.

## Authentication

Clients authenticate by calling:

```text
POST /api/v1/auth/login
```

The returned JWT token is supplied in subsequent requests using:

```http
Authorization: Bearer <jwt-token>
```

## Authorisation

* Read-only endpoints are publicly accessible.
* Endpoints that modify data require the `ADMIN` role.
* Authorisation is enforced through Spring Security and method-level security.

For local development a default administrator account is created through Flyway migrations.

| Username | Password |
| -------- | -------- |
| admin    | admin123 |

---

# REST API

Base path:

```text
/api/v1
```

## Authentication

| Method | Endpoint      | Access |
| ------ | ------------- | ------ |
| POST   | `/auth/login` | Public |

## Artists

| Method | Endpoint                   | Description        | Access |
| ------ | -------------------------- | ------------------ | ------ |
| POST   | `/artists`                 | Create artist      | Admin  |
| GET    | `/artists`                 | List artists       | Public |
| GET    | `/artists/{artistId}`      | Get artist details | Public |
| PATCH  | `/artists/{artistId}/name` | Update artist name | Admin  |

## Tracks

| Method | Endpoint                     | Description        | Access |
| ------ | ---------------------------- | ------------------ | ------ |
| POST   | `/artists/{artistId}/tracks` | Add track          | Admin  |
| GET    | `/artists/{artistId}/tracks` | List artist tracks | Public |

Supports pagination and optional filtering by genre.

## Artist of the Day

| Method | Endpoint             | Description                                    | Access |
| ------ | -------------------- | ---------------------------------------------- | ------ |
| GET    | `/artist-of-the-day` | Return the featured artist for the current day | Public |

Swagger/OpenAPI documentation is available at:

* `/swagger-ui.html`
* `/v3/api-docs`

---

# Data Model

## Artists

| Column     | Type         | Notes                 |
| ---------- | ------------ | --------------------- |
| id         | UUID         | Primary key           |
| name       | VARCHAR(255) | Required              |
| created_at | TIMESTAMPTZ  | Creation timestamp    |
| updated_at | TIMESTAMPTZ  | Last update timestamp |
| version    | BIGINT       | Optimistic locking    |

Indexes

* `(created_at, id)` for Artist of the Day selection
* `(name)` for artist lookup

---

## Tracks

| Column           | Type         | Notes                 |
| ---------------- | ------------ | --------------------- |
| id               | UUID         | Primary key           |
| artist_id        | UUID         | Foreign key           |
| title            | VARCHAR(255) | Required              |
| genre            | VARCHAR(20)  | Stored as string      |
| duration_seconds | INTEGER      | Positive value        |
| created_at       | TIMESTAMPTZ  | Creation timestamp    |
| updated_at       | TIMESTAMPTZ  | Last update timestamp |
| version          | BIGINT       | Optimistic locking    |

Index

* `(artist_id)`

---

# Internationalisation

API messages support localisation using Spring's `MessageSource`.

* Locale is determined from the `Accept-Language` request header.
* English is the default locale.
* German translations are included as an example.
* Missing translations fall back to the default message bundle.

Example:

```bash
curl http://localhost:8080/api/v1/artists/{id} \
  -H "Accept-Language: de"
```

---

# Observability

The application includes several operational features commonly used in production systems.

## Spring Boot Actuator

The following endpoints are enabled:

* `/actuator/health`
* `/actuator/info`
* `/actuator/metrics`
* `/actuator/prometheus`

## Correlation IDs

Each request is assigned (or propagates) an `X-Correlation-Id` to simplify request tracing across logs.

## API Documentation

Swagger UI and OpenAPI documentation are automatically generated.

---

# Testing Strategy

The project contains both unit and integration tests.

| Test Type         | Purpose                                           |
| ----------------- | ------------------------------------------------- |
| Unit Tests        | Validate business logic in isolation              |
| Integration Tests | Verify API, persistence and database interactions |
| Validation Tests  | Verify request validation and error responses     |

Integration tests use **Testcontainers** to execute against a real PostgreSQL database rather than an in-memory database.

---

# Key Design Decisions

## Package-by-Feature

Features are organised by business capability rather than technical layer, improving cohesion and making future enhancements easier.

---

## UUID Primary Keys

UUIDs avoid exposing sequential identifiers and allow identifier generation independently of the database.

---

## Optimistic Locking

Mutable entities include a version column.

Concurrent update conflicts return **HTTP 409 Conflict**, preventing accidental overwriting of changes.

---

## Flyway Database Migrations

Database schema changes are managed using versioned Flyway migrations.

Hibernate is configured to validate the schema rather than generate it automatically.

---

## Genre as an Enum

Genres are represented as a Java enum and stored as strings in the database.

A database `CHECK` constraint ensures only supported values are persisted.

---

## Consistent Error Responses

Errors are returned using Spring's `ProblemDetail` model through centralized exception handling, providing a consistent response format across the API.

---

## Artist of the Day Algorithm

The featured artist is selected deterministically using the current UTC day.

```text
offset = floorMod(epochDayUtc, totalArtists)
```

Artists are ordered by `created_at` and `id`, ensuring every artist appears once during each rotation cycle before the sequence repeats.

This approach requires no scheduled jobs, maintains deterministic behaviour across multiple application instances, and remains stable after application restarts.

---

# Future Enhancements

The following improvements could be considered if the application were to evolve further:

* Artist alias history
* Artist search by alias
* Update and delete track endpoints
* Soft delete support
* User administration APIs
* Redis caching for frequently accessed data
* Full-text search
* Rate limiting
* Audit logging
* Metrics dashboards and distributed tracing
