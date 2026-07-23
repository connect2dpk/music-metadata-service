# Music Metadata Service

REST API implementing the Music Metadata Service described in the `docs/Requirement.md`.

The application provides the following functionality:

* Add a new track to an artist's catalogue
* Update an artist's name
* Retrieve tracks for a specific artist
* Display a rotating **Artist of the Day**

Additional architecture, design decisions, security considerations, and implementation details are documented in `docs/Architecture.md`.

---

## Technology Stack

| Component          | Technology                       |
| ------------------ |----------------------------------|
| Language           | Java 25                          |
| Framework          | Spring Boot 4.0.7                |
| Database           | PostgreSQL                       |
| Persistence        | Spring Data JPA                  |
| Database Migration | Flyway                           |
| Security           | Spring Security + JWT            |
| API Documentation  | OpenAPI / Swagger UI             |
| Testing            | JUnit 5, Mockito, Testcontainers |
| Containerisation   | Docker Compose                   |

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

To keep it easy for the setup and review, the `.env` file has been kept in the project repo without being included in gitignore file

In real production environment, this should not be exposed.


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

```bash
./mvnw test
./mvnw verify
```

Integration tests use Testcontainers, so Docker must be running.

---

## Assumptions

* A track belongs to a single artist.
* Artist names can be updated while artist identifiers remain unchanged.
* The Artist of the Day rotates through all artists in a deterministic cycle.
* Delete operations are outside the scope of this exercise.
