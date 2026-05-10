# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Interactive test harness (mock ANSP consumer) for validating ED-254 (Extended AMAN / Arrival Sequence Service) provider implementations. Authenticates via Keycloak, manages subscriptions through the provider's REST API, connects to the provider's AMQP broker, captures arrival sequence messages, and runs conformance tests against the ED-254 specification.

Part of the SWIM (System Wide Information Management) reference architecture for Red Hat. Depends on shared modules from `swim-developer-validators` (parent POM `swim-validators`).

## Build & Run

```bash
# Prerequisites: install parent/shared modules first (one-time)
make sync                     # pull deps + install into local Maven repo

# Infrastructure (MariaDB on port 3310)
podman compose up -d

# Dev mode (hot-reload on port 8080)
./mvnw quarkus:dev

# Build JAR (skip tests)
./mvnw clean package -DskipTests

# Run tests
./mvnw test

# Single test class
./mvnw test -Dtest=SomeTest

# Coverage report (output: target/site/jacoco/index.html)
./mvnw test jacoco:report
```

Test profile uses H2 in-memory database (`%test` in application.properties) — no external DB needed.

## Key Environment Variables

| Variable | Purpose |
|----------|---------|
| `SWIM_PROVIDER_API_URLS` | Provider REST API base URL(s) |
| `SWIM_PROVIDER_AMQP_HOST` / `_PORT` | Provider's AMQP broker |
| `KEYCLOAK_URL` / `_REALM` / `_CLIENT_ID` | OAuth2/OIDC config |
| `PROXY_MTLS_KEYSTORE_*` / `PROXY_MTLS_TRUSTSTORE_*` | mTLS certs (PKCS12) |

## Architecture

Hexagonal (ports & adapters) with three layers. Java 21, Quarkus, Vert.x Proton (AMQP), MariaDB/Hibernate.

### Domain Layer (`domain/`)

Port interfaces only — no business logic.

- **Inbound ports** (`domain/port/in/`): `ConnectionTrackerPort`, `ConformanceTestPort`, `ConformanceHttpPort`, `MessagePersistencePort`, `MessagePort`, `ProviderSubscriptionPort`, `ConsoleNotificationPort`, `TestScenarioPort`
- **Outbound port** (`domain/port/out/`): `ReceivedMessageRepository`
- **Models** (`domain/model/`): `ReceivedMessage`, `ArrivalSequenceEvent`, `HttpResult`, `MessageData`

### Application Layer (`application/usecase/`)

Use cases that orchestrate domain ports:

- `SubscriptionService` — reacts to subscription lifecycle (activated/paused/deleted), manages AMQP receivers via `ConnectionTrackerPort`
- `ConformanceTestService` — executes ED-254 compliance test scenarios (API-01..04, DM-01..03, WFS-01) via `ConformanceHttpPort`
- `MessageService` — persists incoming AMQP messages, extracts ED-254 ArrivalSequence metadata via `Ed254ArrivalSequenceExtractor`
- `ConsoleService` — SSE broadcast for real-time UI feedback
- `TestScenarioRegistry` — in-memory catalog of conformance test definitions

### Infrastructure Layer (`infrastructure/`)

Concrete adapters:

- **REST** (`rest/`): `ProviderProxyResource` (proxies to real provider under `/api/provider/*`), `ApiResource` (config/health), `ConsoleResource` (SSE), `MessageResource`, `ConformanceTestResource`, `AmqpApiResource`
- **Messaging** (`messaging/`): `UserReceiverLifecycle` (low-level AMQP via Vert.x Proton), `UserConnectionTracker` (connection state + heartbeat), `AmqpSslConfigurator` (mTLS for AMQP), `AmqpConnectionCleanupScheduler`
- **Persistence** (`persistence/`): `ReceivedMessageRepositoryImpl` (Panache/JPA), `ReceivedMessageEntity`, `ReceivedMessageMapper`
- **HTTP Clients** (`client/`): `ProviderHttpClient` (raw HTTP to provider), `ConformanceHttpClient` (adapts to domain `HttpResult`)

### Key Data Flows

**Subscription activation** — REST endpoint notifies `SubscriptionService.onSubscriptionActivated()` → `ConnectionTrackerPort.createReceiver()` → AMQP receiver opens on provider queue.

**Message ingestion** — AMQP broker → `UserReceiverLifecycle` → `MessagePersistencePort.save()` → `MessageService` extracts ArrivalSequence → `ReceivedMessageRepository.insert()` → MariaDB.

**Conformance testing** — REST endpoint → `ConformanceTestService.executeTest()` → `ConformanceHttpPort` (GET/POST/PUT/DELETE) → asserts ED-254 compliance → returns results map.

## Container Build

```bash
make jvm                   # JVM multi-arch image (build + push)
make native                # Full native sequence (amd64 + arm64 + manifest + push)
```

Override registry: `make jvm REGISTRY=quay.io/myorg TAG=v1.2.3`

Helm chart under `src/main/helm/swim-ed254-provider-validator/`.

## Non-Negotiable Rules (from project Cursor rules)

- **No AI authorship**: never add `Co-Authored-By` or any AI tool reference to commits
- **Test integrity**: never change production code to make tests pass — fix tests properly
- **Deployment confirmation**: never execute `oc apply`/`oc create`/`oc delete` without explicit user confirmation
- **Naming semantics**: every artifact name must be unambiguous and specific (e.g., `swim-ed254-provider-validator`, not `swim-validator`)
- **Diagrams**: use Mermaid in markdown, never SVG references
- **One instruction at a time**: never give multiple steps in a single message; wait for confirmation
