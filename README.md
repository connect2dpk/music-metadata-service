# Music Metadata Service

REST API implementing the Music Metadata Service described in the [docs/Requirement.md](docs/Requirement.md).

The application supports the following functionality:

* Add a new track to an artist's catalogue
* Update an artist's name
* Retrieve tracks for a specific artist
* Display a rotating **Artist of the Day**

Further implementation details, architecture decisions, and design assumptions are documented in [docs/Architecture.md](docs/Architecture.md).

---

## Implementation Highlights

* RESTful API for managing artists and tracks
* PostgreSQL persistence using Spring Data JPA
* Database versioning with Flyway migrations
* JWT-based authentication with Role-Based Access Control (RBAC)
* OpenAPI documentation with Swagger UI
* Containerised deployment using Docker Compose
* Global exception handling using Spring `ProblemDetail`
* Health and operational endpoints with Spring Boot Actuator
* Internationalised API messages via `Accept-Language`
* Unit and integration testing with Testcontainers
* Deterministic "Artist of the Day" rotation algorithm

---

## Technology Stack

| Component          | Technology                       |
|-------------------|----------------------------------|
| Language          | Java 25                          |
| Framework         | Spring Boot 4.0.7                |
| Build Tool        | Maven                            |
| Database          | PostgreSQL                       |
| Persistence       | Spring Data JPA                  |
| Database Migration| Flyway                           |
| Security          | Spring Security + JWT            |
| API Documentation | OpenAPI / Swagger UI             |
| Testing           | JUnit 5, Mockito, Testcontainers |
| Containerisation  | Docker Compose                   |
---

## API Endpoints

Base URL:

```text
/api/v1
```

| Method | Endpoint                     | Description         | Access |
| ------ | ---------------------------- | ------------------- | ------ |
| POST   | `/auth/login`                | Authenticate user   | Public |
| POST   | `/artists`                   | Create artist       | Admin  |
| GET    | `/artists`                   | List artists        | Public |
| GET    | `/artists/{artistId}`        | Get artist details  | Public |
| PATCH  | `/artists/{artistId}/name`   | Update artist name  | Admin  |
| POST   | `/artists/{artistId}/tracks` | Add track           | Admin  |
| GET    | `/artists/{artistId}/tracks` | List artist tracks  | Public |
| GET    | `/artist-of-the-day`         | Get featured artist | Public |

Swagger is available after the application starts:

* `http://localhost:8080/swagger-ui.html`
* `http://localhost:8080/v3/api-docs`

---

## Running the Application

### Prerequisites

* Docker
* Docker Compose

### Environment

A .env file is included for easy setup. 
In production, secrets (e.g., DB credentials, JWT secrets) should be managed via environment variables or a secrets manager, not in source control.


### Start

```bash
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

### Default Administrative User

For local development, a default administrator account is created during database initialisation.

| Username | Password |
| -------- | -------- |
| admin    | admin123 |

Authenticate using:

```text
POST /api/v1/auth/login
```

Use the returned JWT token in the `Authorization` header for endpoints requiring administrative access.

---

## Running Tests

On Linux/macOS:

```bash
./mvnw test
./mvnw verify
```

On Windows:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
```

Integration tests use Testcontainers, so Docker must be running.

---

## Assumptions

The following implementation decisions were made where the original requirements were intentionally open-ended:

* A track belongs to a single artist.
* Artist names can be updated while artist identifiers remain unchanged. Alias history is outside the scope of this exercise.
* The Artist of the Day rotates through all artists in a deterministic cycle.
* Delete operations are outside the scope of this exercise.
* Authentication and Role-Based Access Control (RBAC) were added as a production-oriented enhancement.
* Authentication is implemented within the application rather than as a separate service.
* Rate limiting, distributed caching, and asynchronous messaging are intentionally out of scope for this exercise.

