# SureFix Lite — bug-service

| | |
|---|---|
| **Student Name** | Shehan Anujaya |
| **Student Number** | 241711072 |
| **Slack Handle** | — |
| **GCP Project ID** | `surefix-eca` |

## Project Description
Owns **bugs** — the relational core of SureFix Lite. Data lives in **PostgreSQL 17 on Cloud SQL**
(private IP inside the VPC) via Spring Data JPA. A bug carries a severity, a status lifecycle, the
target repository, reporter/assignee and free-form tags (`bug_tags` collection table).

Status lifecycle enforced by `BugService` (invalid moves answer `409 Conflict`):

```
NEEDS_INFO ──▶ AWAITING_APPROVAL ──▶ FIXING ──▶ FIXED ──▶ CLOSED
     ▲                                                     │
     └───────────────────── reopen ────────────────────────┘
```

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/bugs` | Create `{title, description, severity, targetRepo, reporter, assignee, tags[]}` |
| `GET` | `/api/v1/bugs?status=&severity=&repo=&q=` | List, newest first; filters combine with AND, `q` searches title/description |
| `GET` | `/api/v1/bugs/stats` | `{total, open, byStatus, bySeverity}` for the dashboard |
| `GET` | `/api/v1/bugs/{id}` | Get one |
| `PUT` | `/api/v1/bugs/{id}` | Update |
| `PATCH` | `/api/v1/bugs/{id}/status` | `{status}` — validated transition |
| `DELETE` | `/api/v1/bugs/{id}` | Delete |

Errors use one shape across all services:
`{timestamp, status, error, message, path, fieldErrors}` (validation errors list the offending fields).

Part of the [services parent repository](https://github.com/shehan-anujaya/surefix-services).

## Technology Stack
Java 25 · Spring Boot 4.0.8 · Spring Data JPA (Specifications) · PostgreSQL · Bean Validation ·
Spring Cloud Config/Eureka client 2025.1.3 · Micrometer Tracing/Zipkin · JUnit 5 + Mockito · PM2

## Project Structure
```
lk.ijse.eca.surefix.bug
├── controller   REST endpoints
├── service      BugService (state machine, search, stats)
├── repository   JpaRepository + JpaSpecificationExecutor, BugSpecifications
├── entity       Bug (+ Severity, Status)
├── dto          BugRequest, StatusRequest, BugStats
└── exception    ApiError, GlobalExceptionHandler, domain exceptions
```

## Setup / Getting Started
```bash
# local PostgreSQL: docker run -d -p 5432:5432 -e POSTGRES_USER=surefix -e POSTGRES_PASSWORD=surefix -e POSTGRES_DB=surefix postgres:17
mvn test                                  # unit tests (no database needed)
mvn spring-boot:run                       # http://localhost:8081/api/v1/bugs (needs config server + eureka)
mvn -DskipTests package                   # target/bug-service.jar
```
Datasource settings come from the Config Server (`bug-service.yaml`, `bug-service-gcp.yaml`).
On GCP: regional MIG with CPU autoscaling (2–4 instances, 3 zones), PM2-managed, Hikari pool sized
for the Cloud SQL tier. Pushing to `main` builds, tests and rolls the MIG through GitHub Actions.
