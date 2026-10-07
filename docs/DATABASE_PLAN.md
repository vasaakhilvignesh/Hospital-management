# Database Plan (Database-per-Service)

## Overview
To enforce service isolation and boundary integrity, each microservice owns its logical MySQL database. Services must not access another service's database directly; all cross-service data access happens via REST APIs.

## Logical Database Mappings

```
MySQL Instance (localhost:3306)
│
├── hospital_auth        --> Owned by Auth Service
├── hospital_patient     --> Owned by Patient Service
├── hospital_doctor      --> Owned by Doctor Service
├── hospital_appointment --> Owned by Appointment Service
├── hospital_billing     --> Owned by Billing Service
├── hospital_pharmacy    --> Owned by Pharmacy Service
└── hospital_report      --> Owned by Report Service
```

*(Note: Notification Service and Eureka Discovery Server operate statelessly without dedicated persistence).*
