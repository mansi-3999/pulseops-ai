# PulseOps AI — Phase 1

A Java 21 + Spring Boot foundation for an intelligent incident correlation and root-cause analysis platform.

## Included
- Package-by-feature architecture
- PostgreSQL + Flyway
- Incident lifecycle APIs
- Validation and global exception handling
- Swagger/OpenAPI
- Actuator health endpoint
- Docker Compose
- JUnit/Mockito test

## Run
```bash
docker compose up -d
mvn spring-boot:run
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Create an incident
```bash
curl -X POST http://localhost:8080/api/v1/incidents   -H "Content-Type: application/json"   -d '{"title":"Payment timeout spike","description":"DB connection failures increased","severity":"HIGH","detectedAt":"2026-07-04T14:32:18Z"}'
```

## Next phase
Kafka telemetry ingestion, correlation engine, PGVector RAG, Spring AI + Amazon Bedrock, Angular dashboard, AWS deployment.
