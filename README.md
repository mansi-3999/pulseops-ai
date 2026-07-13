# 🚀 PulseOps AI

> **An AI-powered Incident Correlation & Root Cause Analysis Platform built with Java 21, Spring Boot, Apache Kafka, PostgreSQL, Docker, and Amazon Bedrock.**

PulseOps AI is an event-driven backend platform that ingests telemetry from distributed services, correlates related failures into incidents, performs deterministic root cause investigation, and generates AI-assisted operational reports using Amazon Bedrock.

---

## ✨ Why PulseOps AI?

Modern distributed systems generate thousands of telemetry events every minute. During production incidents, engineers often spend valuable time manually correlating failures across multiple services before identifying the actual root cause.

PulseOps AI automates this workflow by:

- 📥 Ingesting telemetry events in real time
- 🔗 Correlating related failures using Trace IDs
- 🚨 Creating incidents automatically
- 🔍 Performing deterministic root cause investigation
- 🤖 Generating AI-powered investigation reports
- 📊 Exposing operational metrics for observability

---

# 🏗 Architecture

<p align="center">
  <img src="docs/images/PulseOpsAi.png"
       alt="PulseOps AI Architecture"
       width="1000"/>
</p>

PulseOps AI follows an event-driven architecture where telemetry events are received through REST APIs, streamed using Apache Kafka, correlated into incidents, investigated using deterministic analysis, and enriched with AI-generated operational insights through Amazon Bedrock. All application data is persisted in PostgreSQL and exposed through REST APIs with built-in observability.

---

# ⚙️ Tech Stack

| Category | Technologies |
|------------|-------------|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Messaging | Apache Kafka |
| Database | PostgreSQL |
| ORM | Spring Data JPA |
| Database Migration | Flyway |
| AI | Amazon Bedrock (Nova Lite) |
| Cloud SDK | AWS SDK v2 |
| Observability | Spring Boot Actuator, Micrometer |
| API Documentation | OpenAPI / Swagger |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito |
| Containerization | Docker & Docker Compose |

---

# 🔥 Key Features

## 🔗 Intelligent Incident Correlation

- Correlates telemetry events using Trace IDs
- Prevents duplicate incident creation
- Groups related failures into a single operational incident
- Tracks correlated event count and affected services

---

## 🔍 Deterministic Root Cause Investigation

Automatically builds investigations by identifying:

- Earliest failing service
- Failure propagation chain
- Cross-service dependencies
- Root cause confidence score
- Supporting investigation evidence

---

## 🤖 AI Investigation Reports

Uses Amazon Bedrock to generate:

- Executive Summary
- Probable Root Cause
- Failure Chain
- Business Impact Assessment
- Recommended Remediation Steps
- Confidence & Caveats

---

## 📊 Observability

- Spring Boot Actuator
- Micrometer Metrics
- Correlation ID Logging
- AI Request Metrics
- Health & Prometheus Endpoints

---

# 📁 Project Structure

```text
pulseops-ai
│
├── src
│   └── main
│       └── java
│           └── com.mansi.pulseops
│               ├── ai
│               ├── telemetry
│               ├── correlation
│               ├── incident
│               ├── investigation
│               ├── observability
│               ├── common
│               └── config
│
├── docs
│   └── images
│
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

---

# 🗄 Database

Application data is stored in PostgreSQL using Flyway-managed schema migrations.

Primary tables include:

- telemetry_events
- incidents
- investigations
- ai_analyses

This enables complete traceability from telemetry ingestion to AI-generated investigation reports.

---

# 🚀 Running Locally

## Clone Repository

```bash
git clone https://github.com/<your-github-username>/pulseops-ai.git
cd pulseops-ai
```

---

## Start Infrastructure

```bash
docker compose up -d
```

---

## Run Application

```bash
mvn clean spring-boot:run
```

Application starts on:

```
http://localhost:8080
```

---

# 📖 API Documentation

### Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

### Spring Boot Actuator

```
http://localhost:8080/actuator
```

---

# 🤖 Example AI Investigation

Each AI-generated investigation includes:

- Executive Summary
- Root Cause Identification
- Failure Chain
- Business Impact
- Recommended Actions
- Confidence Score

The generated report is persisted in PostgreSQL and can be retrieved through REST APIs.

---

# 📊 Observability

PulseOps exposes runtime metrics through Spring Boot Actuator and Micrometer.

Example endpoint:

```
GET /actuator/metrics/pulseops.ai.requests
```

Additional endpoints include:

- `/actuator/health`
- `/actuator/prometheus`
- `/actuator/metrics`

---

# 🚀 Future Enhancements

- Kubernetes deployment
- Grafana dashboards
- OpenTelemetry tracing
- Multi-model AI support
- Role-based authentication
- Incident notification integrations

---

## 🤝 Connect

If you'd like to discuss backend engineering, distributed systems, or AI-powered applications, feel free to connect.

- **LinkedIn:** https://linkedin.com/in/mansisolanki39