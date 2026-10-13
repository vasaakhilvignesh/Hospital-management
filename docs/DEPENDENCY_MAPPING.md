# Dependency Mapping

## Microservices Architecture Dependency Status

### IMPLEMENTED
- **API Gateway (`api-gateway`)** -> `AUTH-SERVICE`
  - Routes `/api/auth/**` to `AUTH-SERVICE`
- **API Gateway (`api-gateway`)** -> `PATIENT-SERVICE`
  - Routes `/api/patients/**` to `PATIENT-SERVICE`
- **API Gateway (`api-gateway`)** -> `DOCTOR-SERVICE`
  - Routes `/api/doctors/**` and `/api/staff/**` to `DOCTOR-SERVICE`
- **API Gateway (`api-gateway`)** -> `APPOINTMENT-SERVICE`
  - Routes `/api/appointments/**` to `APPOINTMENT-SERVICE`
- **APPOINTMENT-SERVICE** -> `PATIENT-SERVICE` (Real REST Inter-Service via Eureka)
  - Calls `GET /api/patients/{id}` to validate patient existence and enforce patient record ownership
- **APPOINTMENT-SERVICE** -> `DOCTOR-SERVICE` (Real REST Inter-Service via Eureka)
  - Calls `GET /api/doctors/{id}` to validate doctor existence
  - Calls `GET /api/doctors/{doctorId}/availability/check` to validate day & time availability slots
- **API Gateway (`api-gateway`)** -> `PHARMACY-SERVICE`
  - Routes `/api/medicines/**`, `/api/inventory/**`, and `/api/prescriptions/**` to `PHARMACY-SERVICE`
- **PHARMACY-SERVICE** -> `PATIENT-SERVICE` (Real REST Inter-Service via Eureka)
  - Calls `GET /api/patients/{id}` to validate patient existence during prescription creation
- **PHARMACY-SERVICE** -> `DOCTOR-SERVICE` (Real REST Inter-Service via Eureka)
  - Calls `GET /api/doctors/{id}` to validate doctor existence during prescription creation
- **API Gateway (`api-gateway`)** -> `BILLING-SERVICE`
  - Routes `/api/bills/**`, `/api/payments/**`, and `/api/insurance/**` to `BILLING-SERVICE`
- **BILLING-SERVICE** -> `PATIENT-SERVICE` (Real REST Inter-Service via Eureka)
  - Calls `GET /api/patients/{id}` to validate patient existence, ownership, and insurance claims
- **BILLING-SERVICE** -> `APPOINTMENT-SERVICE` (Real REST Inter-Service via Eureka)
  - Calls `GET /api/appointments/{id}` to validate appointment reference on consultation billing
- **BILLING-SERVICE** -> `PHARMACY-SERVICE` (Real REST Inter-Service via Eureka)
  - Calls `GET /api/medicines/{id}` to validate pharmacy line-item pricing
- **AUTH-SERVICE** -> `hospital_auth` (Dedicated Logical Persistence)
- **PATIENT-SERVICE** -> `hospital_patient` (Dedicated Logical Persistence)
- **DOCTOR-SERVICE** -> `hospital_doctor` (Dedicated Logical Persistence)
- **APPOINTMENT-SERVICE** -> `hospital_appointment` (Dedicated Logical Persistence)
- **PHARMACY-SERVICE** -> `hospital_pharmacy` (Dedicated Logical Persistence)
- **BILLING-SERVICE** -> `hospital_billing` (Dedicated Logical Persistence)
- **API Gateway (`api-gateway`)** -> `REPORT-SERVICE`
  - Routes `/api/reports/**` to `REPORT-SERVICE`
- **REPORT-SERVICE** -> `PATIENT-SERVICE` (Real REST Inter-Service via Eureka)
  - Fetches patient demographic & medical history for medical summaries
- **REPORT-SERVICE** -> `DOCTOR-SERVICE` (Real REST Inter-Service via Eureka)
  - Fetches total active doctors for hospital dashboard summary
- **REPORT-SERVICE** -> `APPOINTMENT-SERVICE` (Real REST Inter-Service via Eureka)
  - Aggregates total, pending, and completed appointment metrics
- **REPORT-SERVICE** -> `BILLING-SERVICE` (Real REST Inter-Service via Eureka)
  - Aggregates patient billing statements, total billed, paid, and outstanding balances
- **REPORT-SERVICE** -> `PHARMACY-SERVICE` (Real REST Inter-Service via Eureka)
  - Aggregates low stock alerts and pending prescription counts
- **API Gateway (`api-gateway`)** -> `NOTIFICATION-SERVICE`
  - Routes `/api/notifications/**` to `NOTIFICATION-SERVICE`
- **NOTIFICATION-SERVICE** -> `PATIENT-SERVICE` (Real REST Inter-Service via Eureka)
  - Validates patient existence and resolves patient ownership for notifications
- **REPORT-SERVICE** -> `hospital_report` (Dedicated Logical Persistence)
- **NOTIFICATION-SERVICE** -> `hospital_notification` (Dedicated Logical Persistence)
- **Eureka Discovery Server (`discovery-server`)**:
  - Registered & Active: `API-GATEWAY`, `AUTH-SERVICE`, `PATIENT-SERVICE`, `DOCTOR-SERVICE`, `APPOINTMENT-SERVICE`, `PHARMACY-SERVICE`, `BILLING-SERVICE`, `REPORT-SERVICE`, `NOTIFICATION-SERVICE`
- **Config Server (`config-server`)**
  - Centralized native repository available for distributed services
- **AUTH-SERVICE (JWT Issuer)** -> **ALL SERVICES (JWT Verifiers)**
  - Stateless signature & claim validation (`userId`, `role`)
  - Enforces RBAC, receptionist/doctor/admin reporting management, patient report isolation, and user notification isolation

---

### PLANNED
- None (All core backend business microservices Phase 1 through Phase 8 are fully implemented)
