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

## Planned Services
1. `discovery-server` (Port 8761)
2. `api-gateway` (Port 8080)
3. `config-server` (Port 8888)
4. `auth-service` (Port 8081)
5. `patient-service` (Port 8082)
6. `doctor-service` (Port 8083)
7. `appointment-service` (Port 8084)
8. `billing-service` (Port 8085)
9. `pharmacy-service` (Port 8086)
10. `report-service` (Port 8087)
11. `notification-service` (Port 8088)

## Project Status
**Phase 0 — Project Foundation: IN PROGRESS**
