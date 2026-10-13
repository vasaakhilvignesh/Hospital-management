# API Plan

## Overview
All external requests route through the Spring Cloud API Gateway (`http://localhost:8080`).

---

## Phase 2: Authentication & Authorization Endpoints (`auth-service`)

### 1. User Registration
- **Method & Path**: `POST /api/auth/register` (routed via Gateway: `http://localhost:8080/api/auth/register`)
- **Authentication**: Public (No JWT required)
- **Request Body**:
```json
{
  "username": "newuser",
  "password": "password123",
  "fullName": "New User",
  "email": "user@hospital.com",
  "phone": "555-1234",
  "role": "PATIENT"
}
```
- **Response**: `201 Created`
```json
{
  "id": 6,
  "username": "newuser",
  "fullName": "New User",
  "email": "user@hospital.com",
  "phone": "555-1234",
  "role": "PATIENT",
  "enabled": true,
  "createdAt": "2026-10-07T23:10:00"
}
```

### 2. User Login
- **Method & Path**: `POST /api/auth/login` (routed via Gateway: `http://localhost:8080/api/auth/login`)
- **Authentication**: Public (No JWT required)
- **Request Body**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **Response**: `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "username": "admin",
  "fullName": "Admin User",
  "role": "ADMIN"
}
```

### 3. Get Current Authenticated User
- **Method & Path**: `GET /api/auth/me` (routed via Gateway: `http://localhost:8080/api/auth/me`)
- **Authentication**: `Bearer <token>` required
- **Response**: `200 OK` (Returns current user's profile)

---

## Gateway Routing Table & Planned Future Endpoint Groups

| Endpoint Group | Target Microservice | Description |
| :--- | :--- | :--- |
| `/api/auth/**` | `AUTH-SERVICE` | User registration, login, token refresh, and profile retrieval |
| `/api/patients/**` | `PATIENT-SERVICE` | Patient records, demographics, and medical histories |
| `/api/doctors/**` | `DOCTOR-SERVICE` | Doctor catalog, specialties, and availability schedules |
| `/api/staff/**` | `DOCTOR-SERVICE` | Hospital staff management and shifts |
| `/api/appointments/**` | `APPOINTMENT-SERVICE` | Booking, rescheduling, status updates, and cancellations |
| `/api/bills/**` | `BILLING-SERVICE` | Invoice generation, line items, and invoice lookup |
| `/api/payments/**` | `BILLING-SERVICE` | Payment processing and receipts |
| `/api/medicines/**` | `PHARMACY-SERVICE` | Pharmacy catalog and medicine management |
| `/api/prescriptions/**` | `PHARMACY-SERVICE` | Doctor prescriptions and dispensing |
| `/api/inventory/**` | `PHARMACY-SERVICE` | Stock levels, low-stock warnings, and expiry alerts |
| `/api/reports/**` | `REPORT-SERVICE` | Aggregated analytics, patient records, and billing summaries |
| `/api/notifications/**`| `NOTIFICATION-SERVICE`| Notifications and alert events |
