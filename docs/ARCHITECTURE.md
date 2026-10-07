# Hospital Management System — Architecture Overview

## 1. System Architecture

```
React Frontend
  |
  v
API Gateway (Spring Cloud Gateway : 8080)
  |
  +---- Auth Service         (:8081)
  +---- Patient Service      (:8082)
  +---- Doctor Service       (:8083)
  +---- Appointment Service  (:8084)
  +---- Billing Service      (:8085)
  +---- Pharmacy Service     (:8086)
  +---- Report Service       (:8087)
  +---- Notification Service (:8088)

Infrastructure:
- Eureka Discovery Server (:8761) provides service registration & discovery.
- Config Server (:8888) centralizes external configurations.
- Logical database-per-service pattern using MySQL.
```

## 2. Service Responsibilities

| Service | Responsibility |
| :--- | :--- |
| **Discovery Server** | Service registry and discovery (Netflix Eureka) |
| **API Gateway** | Single entry point, routing, rate limiting, and CORS |
| **Config Server** | Centralized configuration management |
| **Auth Service** | User registration, authentication, JWT issuing, RBAC |
| **Patient Service** | Patient registration, records, medical history, diagnosis/treatment |
| **Doctor Service** | Doctor profiles, staff profiles, availability, and scheduling |
| **Appointment Service** | Appointment booking, rescheduling, and cancellation |
| **Billing Service** | Invoices, charges, payment processing, insurance info |
| **Pharmacy Service** | Medicine catalog, prescriptions, inventory, low stock & expiry tracking |
| **Report Service** | Patient records, billing summaries, appointment analytics |
| **Notification Service** | Appointment reminders, billing notifications, system alerts |

## 3. MVC Architecture (Per Service)

Every business microservice adheres to the MVC pattern:

```
Controller  -->  Service  -->  Repository  -->  Database
```

### Standard Package Structure:
- `controller/`: REST endpoints and request mapping (thin controllers)
- `service/`: Core business logic and transactions
- `repository/`: Spring Data JPA interfaces for database operations
- `entity/` or `model/`: JPA entities representing domain tables
- `dto/`: Request/Response Data Transfer Objects
- `exception/`: Domain-specific exceptions and `@ControllerAdvice` global handler
- `config/`: Security, Web, and Bean configurations

## 4. Factory Method Design Pattern

To be implemented in the `auth-service`:

```
User (Interface / Abstract Class)
 ├── Admin
 ├── Patient
 └── Staff

UserFactory (Abstract Creator)
 ├── AdminFactory
 ├── PatientFactory
 └── StaffFactory

RegistrationService (Client)
```

**Design Rule**: `RegistrationService` will delegate object creation to the appropriate concrete `UserFactory` rather than directly instantiating concrete user classes with `new Admin()` or `new Patient()`. This ensures adherence to the Open-Closed and Single-Responsibility principles.
