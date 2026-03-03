# PR Impact Test Project

> **⚠️ Hinweis:** Dieses Projekt dient ausschließlich zum Testen von PR-Impact-Analysetools.

## Projektbeschreibung

Dieses Spring Boot-Projekt wurde speziell entwickelt, um PR-Impact-Analysetools zu testen. Es enthält absichtlich markierte Stellen für verschiedene Änderungsszenarien, die typische Code-Änderungen in realen Projekten simulieren.

## Technologie-Stack

- **Framework:** Spring Boot 3.2.3
- **Build-Tool:** Gradle 8.5
- **Java-Version:** 17
- **Datenbank:** H2 (In-Memory)
- **Security:** Spring Security
- **Monitoring:** Spring Actuator

## Projektstruktur

```
src/main/java/com/primpact/testproject/
├── PrImpactTestApplication.java     # Main Application
├── client/
│   └── FakePaymentClient.java       # External Service Mock
├── config/
│   └── SecurityConfig.java          # Security Configuration
├── controller/
│   └── OrderController.java         # REST API Controller
├── entity/
│   └── OrderEntity.java             # JPA Entity
├── model/
│   ├── OrderRequest.java            # Request DTO
│   ├── OrderResponse.java           # Response DTO
│   └── UserModel.java               # User Model
├── repository/
│   └── OrderRepository.java         # Data Access Layer
└── service/
    └── OrderService.java            # Business Logic Layer
```

## Analyse-Punkte (Change Scenarios)

### 🔒 SECURITY (CHANGE SCENARIO: SECURITY)
Markierte Stellen für Security-relevante Änderungen:
- `SecurityConfig.java` - SecurityFilterChain, CSRF, Authorization Rules
- `UserModel.java` - Rolle-basierte Logik

### 💼 BUSINESS LOGIC (CHANGE SCENARIO: BUSINESS LOGIC)
Markierte Stellen für Business-Logik-Änderungen:
- `OrderService.java` - `isDiscountApplicable()`, `categorizeOrder()`, `processOrder()`
- `FakePaymentClient.java` - External Payment Service
- `OrderRepository.java` - Custom Queries

### ⚙️ CONFIG (CHANGE SCENARIO: CONFIG)
Markierte Stellen für Konfigurations-Änderungen:
- `application.yml` - Feature Flags, Actuator Exposure, Logging Level
- `OrderController.java` - Export Status Endpoint
- `OrderService.java` - Feature Flag Usage

### 🔗 API BREAKING (CHANGE SCENARIO: API BREAKING)
Markierte Stellen für API-Breaking Changes:
- `OrderController.java` - REST Endpoints
- `OrderEntity.java` - Entity Fields
- `OrderRequest.java` / `OrderResponse.java` - DTOs

## Security Test Points (SECURITY TEST POINT)

Kommentare mit `// SECURITY TEST POINT` markieren kritische Sicherheitskonfigurationen:
1. CSRF-Konfiguration (aktiviert, nicht deaktiviert)
2. Authorization Rules (keine `permitAll` für sensible Endpoints)
3. UserDetailsService mit Rollen

## Konfigurations-Drift-Szenarien

Die `application.yml` enthält markierte Stellen für typische Config-Drift-Szenarien:

| Konfiguration | Standard | Drift-Szenario |
|---------------|----------|----------------|
| `feature.export.enabled` | `false` | → `true` |
| `management.endpoints.web.exposure.include` | `health` | → `*` |
| `logging.level.root` | `INFO` | → `DEBUG` |

## Build & Run

### Kompilieren
```bash
./gradlew build
```

### Tests ausführen
```bash
./gradlew test
```

### Anwendung starten
```bash
./gradlew bootRun
```

### API testen (mit Basic Auth)
```bash
# Health Check
curl -u user:password http://localhost:8080/actuator/health

# Order erstellen
curl -u user:password -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"amount": 1500}'

# Alle Orders abrufen
curl -u user:password http://localhost:8080/api/orders
```

## Test Coverage

- `OrderServiceTest` - Unit Tests für Business Logic
  - `isDiscountApplicable()` Tests
  - `categorizeOrder()` Tests (verschachtelte ifs)
  - `processOrder()` Tests (mit Payment Client Mock)
- `OrderControllerTest` - Integration Tests mit MockMvc
  - API Endpoint Tests
  - Security Tests (Authentication, CSRF)

## Branches für Change-Szenarien

| Branch | Beschreibung |
|--------|-------------|
| `main` | Basis-Projekt |
| `branch-security-relaxation` | Security gelockert (permitAll, CSRF disabled) |
| `branch-business-logic-change` | Discount-Logik geändert |
| `branch-api-breaking` | API-Breaking Changes |
| `branch-config-drift` | Konfigurationsänderungen |

## Credentials (Test-Benutzer)

| Username | Password | Rollen |
|----------|----------|--------|
| `user` | `password` | USER |
| `admin` | `admin` | USER, ADMIN |

---

**Erstellt für:** PR-Impact-Analyse-Tool Testing
