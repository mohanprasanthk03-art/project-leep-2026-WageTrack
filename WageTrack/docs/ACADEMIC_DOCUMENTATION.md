# WageTrack – Academic Documentation

## Problem statement
Rural worksites may track attendance and daily wages manually, making records difficult to verify and potentially leading to disputes or delayed payment. WageTrack provides a backend system to store worker, worksite, attendance, payroll summary, and payment information in a relational database.

## Objective
To implement a beginner-friendly Spring Boot REST backend that registers workers and worksites, records attendance and overtime, calculates payable wages for a date range, and tracks payments with validation and service-layer business rules.

## Functional requirements
1. Create, read, update, and deactivate workers.
2. Create, read, update, and deactivate worksites.
3. Record, read, update, and delete attendance.
4. Validate worker/worksite references, attendance status, overtime, and uniqueness.
5. Calculate weekly wages from stored attendance.
6. Create, read, and update payment records.
7. Derive payment status from the paid amount.
8. Return meaningful HTTP status codes and error details.

## Non-functional requirements
- Maintainable layered structure.
- Relational integrity through primary keys, foreign keys, unique and not-null constraints.
- Input validation and predictable errors.
- Monetary arithmetic uses `BigDecimal`.
- Understandable configuration and straightforward local execution.
- No microservices or unnecessary external integrations.

## System architecture
A client such as Postman sends HTTP/JSON requests to Spring MVC controllers. Controllers validate DTOs and call services. Services apply business rules and call Spring Data JPA repositories. Hibernate maps entities to MySQL tables. Responses are mapped to DTOs so internal entity relationships are not exposed.

## High-Level Design (HLD)
Components:
- API client: submits JSON and reads responses.
- Controller layer: Worker, Worksite, Attendance, Payroll, Payment.
- Service layer: CRUD operations, attendance validation, payroll computation, payment-state calculation.
- Repository layer: database access interfaces.
- MySQL: stores four primary entities and relationships.
- Global exception handler: converts errors into a common JSON structure.

## Low-Level Design (LLD)
- DTO records receive validated input and shape output.
- Entity classes map to tables with JPA annotations.
- Repository interfaces extend `JpaRepository`.
- Services use constructor injection and transaction boundaries.
- Payroll service retrieves attendance in an inclusive date range, computes regular wage by status, computes overtime, and returns a summary.
- Payment service recomputes payable wage from attendance and derives status from `amountPaid`.
- Worker and worksite delete operations set `active=false` to preserve historical attendance/payment references.

## ER diagram description
- Worker (`id` PK) has a one-to-many relationship with Attendance (`worker_id` FK).
- Worksite (`id` PK) has a one-to-many relationship with Attendance (`worksite_id` FK).
- Worker (`id` PK) has a one-to-many relationship with PaymentRecord (`worker_id` FK).
- Attendance has a composite unique constraint on (`worker_id`, `attendance_date`).
- PaymentRecord has a composite unique constraint on (`worker_id`, `week_start_date`).

## UML class diagram description
`WorkerController` depends on `WorkerService`, which depends on `WorkerRepository`. The same pattern is used for Worksite and Attendance. `PayrollController` calls `PayrollService`, which uses `WorkerRepository`, `AttendanceRepository`, and `PayrollConfig`. `PaymentController` calls `PaymentService`, which uses `PaymentRecordRepository`, `WorkerRepository`, and `PayrollService`. Entities associate as defined in the ER description; DTO records carry request/response data.

## Use case diagram description
Primary actor: Site Clerk / Administrator.
Use cases: manage workers, manage worksites, record attendance, correct attendance, view attendance, calculate weekly payroll, record payment, update payment, and review payment status. Payroll is calculated from recorded attendance rather than manually entered totals.

## DFD Level 0 description
External entity (Site Clerk) sends worker, worksite, attendance, payroll, and payment requests to the WageTrack system. The system reads/writes its database and returns records, summaries, or validation errors.

## DFD Level 1 description
1. Worker management receives worker details, validates them, and stores worker data.
2. Worksite management receives site details and stores site data.
3. Attendance management verifies worker and site references, checks status/overtime and duplicate date, then stores attendance.
4. Payroll calculation reads worker wage and attendance in the requested date range, computes regular wage and overtime, and returns a summary.
5. Payment management calculates the payable amount from payroll, validates amount paid, derives payment status, and stores the payment record.

## Database schema summary
### workers
`id BIGINT PK AUTO_INCREMENT`, `worker_code VARCHAR(30) UNIQUE NOT NULL`, `name VARCHAR(100) NOT NULL`, `phone VARCHAR(16) NOT NULL`, `address VARCHAR(500)`, `daily_wage DECIMAL(10,2) NOT NULL`, `active BOOLEAN NOT NULL`, `created_at DATETIME NOT NULL`.

### worksites
`id BIGINT PK AUTO_INCREMENT`, `site_code VARCHAR(30) UNIQUE NOT NULL`, `site_name VARCHAR(120) NOT NULL`, `location VARCHAR(200) NOT NULL`, `description VARCHAR(500)`, `active BOOLEAN NOT NULL`.

### attendance
`id BIGINT PK AUTO_INCREMENT`, `worker_id BIGINT FK NOT NULL`, `worksite_id BIGINT FK NOT NULL`, `attendance_date DATE NOT NULL`, `status VARCHAR(20) NOT NULL`, `overtime_hours DECIMAL(6,2) NOT NULL`, `created_at DATETIME NOT NULL`; unique (`worker_id`, `attendance_date`).

### payment_records
`id BIGINT PK AUTO_INCREMENT`, `worker_id BIGINT FK NOT NULL`, `week_start_date DATE NOT NULL`, `week_end_date DATE NOT NULL`, `payable_amount DECIMAL(12,2) NOT NULL`, `amount_paid DECIMAL(12,2) NOT NULL`, `payment_date DATE`, `payment_status VARCHAR(20) NOT NULL`, `remarks VARCHAR(500)`; unique (`worker_id`, `week_start_date`).

## API documentation
Base URL: `http://localhost:8080`.
- Workers: `POST/GET /api/workers`, `GET/PUT/DELETE /api/workers/{id}`.
- Worksites: `POST/GET /api/worksites`, `GET/PUT/DELETE /api/worksites/{id}`.
- Attendance: `POST/GET /api/attendance`, `GET/PUT/DELETE /api/attendance/{id}`.
- Payroll: `GET /api/payroll/worker/{workerId}/weekly?weekStartDate=YYYY-MM-DD&weekEndDate=YYYY-MM-DD`.
- Payments: `POST/GET /api/payments`, `GET/PUT /api/payments/{id}`.
Use JSON request bodies for create/update. Dates use ISO `YYYY-MM-DD`. Attendance enum values are `PRESENT`, `HALF_DAY`, `ABSENT`.

## Business rules
- Present day = daily wage.
- Half day = half daily wage.
- Absent = zero.
- Overtime = overtime hours × (daily wage / standard workday hours) × overtime multiplier.
- Default standard day = 8 hours; multiplier = 1.5.
- No negative overtime; absent cannot have overtime.
- One attendance per worker/date.
- Weekly payable is the sum of attendance wages and overtime in the inclusive range.
- Payment amount must be non-negative and not exceed calculated payable.
- Payment status is calculated as PENDING, PARTIALLY_PAID, or PAID.
- Deactivated workers/sites cannot be assigned new attendance; inactive workers cannot receive new payment records.

## Validation strategy
Jakarta Bean Validation annotations are applied to request DTOs (`@NotBlank`, `@NotNull`, `@Positive`, `@PositiveOrZero`, `@Size`, `@Pattern`). Service methods also enforce cross-field and database-dependent rules, such as absent/overtime consistency, active records, duplicate attendance, valid references, date order, and payment cap.

## Exception handling strategy
`@RestControllerAdvice` centralizes errors. Missing IDs return 404; invalid input and business rules return 400; duplicate attendance and database uniqueness conflicts return 409; malformed JSON/enum/date values return 400; unexpected errors return a generic 500 response. Error payload contains timestamp, status, error, message, and request path.

## Testing strategy
- Unit test: sample weekly payroll arithmetic.
- API tests with Postman: CRUD happy paths, not-found IDs, invalid fields, invalid foreign keys, duplicate attendance, absent with overtime, negative overtime, payment over cap, partial payment, full payment, and pending payment.
- DB checks: inspect generated tables, primary/foreign keys, unique constraints, and persisted values in MySQL Workbench.
- Regression: run `mvn clean test` after changes and repeat relevant Postman cases.

## Assumptions and scope choices
- The provided overtime value represents overtime hours already worked beyond the regular eight-hour workday; the API does not store total hours worked.
- A week is represented by the inclusive start/end dates supplied by the client; the backend does not force Monday-to-Sunday or exactly seven days.
- Payment records are one summary per worker per week-start date. `payableAmount` is calculated by the service, not accepted from client input.
- Worker and worksite deletes are soft deactivations to retain historical records.
