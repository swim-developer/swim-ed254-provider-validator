# swim-ed254-provider-validator — Knowledge Base


## What This Is

**Mock ANSP (conformance test client) for validating the ED-254 Provider.** Simulates a downstream ANSP subscriber, validates the provider against ED-254 conformance requirements.

## What It Does

| Component | Purpose |
|-----------|---------|
| **Subscription UI** | Creates/manages subscriptions against the provider REST API |
| **AMQP Consumer** | Connects to provider's Artemis, receives arrival sequence events |
| **Conformance Validator** | ED-254 compliance test scenarios |

## Provider Connection Config

```yaml
swimServiceBaseURL: "https://ed254-provider-<namespace>.apps.<cluster>"
amqpBrokerHost: "ed254-provider-artemis-<namespace>.apps.<cluster>"
```

## Build & Run

```bash
./mvnw clean package -DskipTests
quarkus dev
```
