# Hospital Management System

## Technology Stack
- **Frontend**: React.js, React Router, Axios, Modern Responsive CSS
- **Backend**: Java 21, Spring Boot, Spring Cloud, Spring Data JPA, Spring Security, JWT
- **Microservices**: Spring Cloud Gateway, Eureka Service Discovery, Spring Cloud Config
- **Database**: MySQL (Database-per-Service)
- **Testing**: JUnit 5, Mockito
- **Version Control**: Git / GitHub

## Architecture & Design Patterns
- **Microservices Architecture**: Decentralized services organized by business domain
- **MVC Architecture**: Layered separation (Controller -> Service -> Repository) per service
- **Factory Method Design Pattern**: Dedicated polymorphic creation for User roles (Admin, Patient, Staff)

## Implemented Services & Ports
1. `discovery-server` (Port 8761) — Eureka Discovery Server
2. `config-server` (Port 8888) — Spring Cloud Config Server
3. `api-gateway` (Port 8080) — Spring Cloud Gateway (Single Entry Point)
4. `auth-service` (Port 8081) — JWT Authentication & RBAC (Factory Method pattern)
5. `patient-service` (Port 8082) — Patient profiles, medical history & immunizations
6. `doctor-service` (Port 8083) — Doctors, departments, staff & availability schedules
7. `appointment-service` (Port 8084) — Appointment bookings & lifecycle management
8. `pharmacy-service` (Port 8085) — Medicine stock, batch inventory & prescriptions
9. `billing-service` (Port 8086) — Invoices, payments & insurance claims
10. `report-service` (Port 8087) — Aggregated clinical reports & dashboard analytics
11. `notification-service` (Port 8088) — User notifications & read receipt ledger
12. `frontend` (Port 3000) — React 19 + TypeScript + Vite responsive dashboard

## Project Status
**Phase 10 — Final Integration, Stabilization, UI Polish, & Review Readiness: COMPLETE**
- All 11 backend microservices running & registered with Eureka
- API Gateway strictly acting as the unified front door
- React frontend fully operational on http://localhost:3000
- Full role-based workflows (Admin, Doctor, Receptionist, Staff, Patient) verified end-to-end
