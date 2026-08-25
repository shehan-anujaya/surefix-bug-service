# SureFix Lite — bug-service

| | |
|---|---|
| **Student Name** | Shehan Anujaya |
| **Student Number** | 241711072 |
| **Slack Handle** | — |
| **GCP Project ID** | `surefix-eca` |

## Project Description
Owns **bugs** — the relational core of SureFix Lite. Data lives in **PostgreSQL 17 on Cloud SQL**
(private IP inside the VPC) via Spring Data JPA.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/bugs` | Create `{title, description, severity, targetRepo}` |
| `GET` | `/api/v1/bugs` | List (newest first) |
| `GET` | `/api/v1/bugs/{id}` | Get one |
| `PUT` | `/api/v1/bugs/{id}` | Update |
| `PATCH` | `/api/v1/bugs/{id}/status` | `{status}` = `NEEDS_INFO · AWAITING_APPROVAL · FIXING · FIXED · CLOSED` |
| `DELETE` | `/api/v1/bugs/{id}` | Delete |

Part of the [services parent repository](https://github.com/shehan-anujaya/surefix-services).

## Technology Stack
Java 25 · Spring Boot 4.0.8 · Spring Data JPA · PostgreSQL · Spring Cloud Config/Eureka client 2025.1.3 · Micrometer Tracing/Zipkin · PM2

## Setup / Getting Started
```bash
# local PostgreSQL: docker run -d -p 5432:5432 -e POSTGRES_USER=surefix -e POSTGRES_PASSWORD=surefix -e POSTGRES_DB=surefix postgres:17
mvn spring-boot:run                       # http://localhost:8081/api/v1/bugs (needs config server + eureka)
mvn -DskipTests package                   # target/bug-service.jar
```
Datasource settings come from the Config Server (`bug-service.yaml`, `bug-service-gcp.yaml`).
On GCP: regional MIG with CPU autoscaling (2–4 instances, 3 zones), PM2-managed.
