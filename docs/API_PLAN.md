# API Plan

## Overview
All external requests route through the Spring Cloud API Gateway (`http://localhost:8080`).

## Gateway Routing Table & Endpoint Groups

| Endpoint Group | Target Microservice | Description |
| :--- | :--- | :--- |
| `/api/auth/**` | `auth-service` | User registration, login, token refresh, and profile retrieval |
| `/api/patients/**` | `patient-service` | Patient records, demographics, and medical histories |
| `/api/doctors/**` | `doctor-service` | Doctor catalog, specialties, and availability schedules |
| `/api/staff/**` | `doctor-service` | Hospital staff management and shifts |
| `/api/appointments/**` | `appointment-service` | Booking, rescheduling, status updates, and cancellations |
| `/api/bills/**` | `billing-service` | Invoice generation, line items, and invoice lookup |
| `/api/payments/**` | `billing-service` | Payment processing and receipts |
| `/api/medicines/**` | `pharmacy-service` | Pharmacy catalog and medicine management |
| `/api/prescriptions/**` | `pharmacy-service` | Doctor prescriptions and dispensing |
| `/api/inventory/**` | `pharmacy-service` | Stock levels, low-stock warnings, and expiry alerts |
| `/api/reports/**` | `report-service` | Aggregated analytics, patient records, and billing summaries |
| `/api/notifications/**`| `notification-service` | Notifications and alert events |
