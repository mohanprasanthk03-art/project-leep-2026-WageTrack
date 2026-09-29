# WageTrack – Daily Wage Worker Attendance and Payment Tracker

## 1. Problem statement
Rural worksites such as construction sites and farms often record worker attendance and wages manually. This can cause payment disputes and delayed settlements. WageTrack provides a small REST backend to register workers and worksites, record attendance and overtime, calculate weekly payable wages, and record payments.

## 2. Objective and features
- Register wage workers with a daily wage rate and active status.
- Manage worksites.
- Record PRESENT, HALF_DAY, or ABSENT attendance and overtime.
- Enforce attendance and payment business rules in services.
- Calculate a worker's payable wage over an inclusive date range.
- Record one payment summary per worker and week-start date, including partial or full payment.
- Use MySQL, JPA, Bean Validation, DTOs, and centralized error responses.

## 3. Technology stack
Java 17, Spring Boot 3.4.5, Spring Web, Spring Data JPA, Hibernate, Jakarta Bean Validation, MySQL 8+, Maven, and Postman.

## 4. Architecture
`Controller → Service → Repository → MySQL`
- **Controller:** maps HTTP requests and responses.
- **Service:** applies business rules and calculations.
- **Repository:** performs database operations through Spring Data JPA.
- **Entity:** represents a database table.
- **DTO:** defines request/response shapes and prevents entity relationships from being serialized recursively.
- **RestControllerAdvice:** returns consistent error responses.

## 5. Database design
Four main tables:
- `workers`: worker identity, unique worker code, contact information, daily wage, active flag, creation time.
- `worksites`: unique site code, name, location, description, active flag.
- `attendance`: worker FK, worksite FK, date, status, overtime hours, creation time. Unique `(worker_id, attendance_date)`.
- `payment_records`: worker FK, week start/end, calculated payable amount, amount paid, payment date, status, remarks. Unique `(worker_id, week_start_date)`.

Relationships: Worker 1-to-many Attendance; Worksite 1-to-many Attendance; Worker 1-to-many PaymentRecord. Attendance and PaymentRecord each have a many-to-one relationship to their parent. Worker/worksite deletion is implemented as deactivation to preserve historical records.

## 6. Business rules and calculation choices
- PRESENT earns the full daily wage; HALF_DAY earns half; ABSENT earns zero.
- `hourlyWage = dailyWage / standardWorkdayHours`.
- `overtimePay = overtimeHours × hourlyWage × overtimeMultiplier`.
- Defaults are 8 standard workday hours and 1.5 overtime multiplier, configurable in `application.properties`.
- `overtimeHours` means approved overtime hours beyond the standard workday, not total hours worked.
- ABSENT with overtime greater than zero is rejected; negative overtime is rejected.
- A worker can have only one attendance row per date, even if a different worksite is supplied.
- Weekly date range is inclusive. The API accepts any valid start/end range; the client normally supplies seven dates.
- Payment payable amount is recalculated by the service from attendance. The client cannot set it directly.
- Payment status is derived: zero paid = PENDING; between zero and payable = PARTIALLY_PAID; equal to payable = PAID. Payment cannot exceed payable.
- Worker/worksite deactivation prevents new attendance; worker deactivation also prevents new payments.

## 7. API endpoints
All endpoints use `http://localhost:8080`.

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/workers` | Create worker |
| GET | `/api/workers` | List workers |
| GET | `/api/workers/{id}` | Get worker |
| PUT | `/api/workers/{id}` | Update worker |
| DELETE | `/api/workers/{id}` | Deactivate worker |
| POST | `/api/worksites` | Create worksite |
| GET | `/api/worksites` | List worksites |
| GET | `/api/worksites/{id}` | Get worksite |
| PUT | `/api/worksites/{id}` | Update worksite |
| DELETE | `/api/worksites/{id}` | Deactivate worksite |
| POST | `/api/attendance` | Record attendance |
| GET | `/api/attendance` | List attendance |
| GET | `/api/attendance/{id}` | Get attendance |
| PUT | `/api/attendance/{id}` | Update attendance |
| DELETE | `/api/attendance/{id}` | Delete attendance |
| GET | `/api/payroll/worker/{workerId}/weekly?weekStartDate=YYYY-MM-DD&weekEndDate=YYYY-MM-DD` | Calculate weekly payable |
| POST | `/api/payments` | Create payment record |
| GET | `/api/payments` | List payment records |
| GET | `/api/payments/{id}` | Get payment |
| PUT | `/api/payments/{id}` | Update payment |

Successful create returns 201, reads/updates return 200, deletes return 204. Validation/business errors return 400, missing records 404, uniqueness conflicts 409, unexpected errors 500.

## 8. Configure MySQL
1. Start MySQL.
2. Create the database:
   ```sql
   CREATE DATABASE wagetrack_db;
   ```
3. Edit `src/main/resources/application.properties`. Set `spring.datasource.username` and set the password through environment variable `DB_PASSWORD` (or replace `YOUR_PASSWORD` locally; never commit a real password).
4. Hibernate `ddl-auto=update` creates/updates the tables from entities for this assessment project.

## 9. Run the project
Install JDK 17 and Maven, then from the project root:
```bash
mvn clean test
mvn spring-boot:run
```
Or package and run:
```bash
mvn clean package
java -jar target/wagetrack-1.0.0.jar
```
Open Postman and use `http://localhost:8080`. Open the project folder in IntelliJ IDEA as a Maven project. Wait for dependency import, configure the JDK as 17, then run `WageTrackApplication`.

### Optional demo data
After the application has started once (so Hibernate has created the tables), you can run `db/seed-data.sql` against an empty `wagetrack_db` in MySQL Workbench. It inserts three fictional workers, two worksites, attendance across seven days, and payment examples. Use it only on a fresh database because it uses fixed IDs and codes.

## 10. Postman quick start
For JSON requests add header `Content-Type: application/json`.

### Create worker
`POST /api/workers`
```json
{
  "workerCode": "W001",
  "name": "Ravi Kumar",
  "phone": "9876543210",
  "address": "Mysuru, Karnataka",
  "dailyWage": 800,
  "active": true
}
```
Expected: 201 Created with generated `id`, worker fields, and `createdAt`.

### Create worksite
`POST /api/worksites`
```json
{
  "siteCode": "S001",
  "siteName": "Canal Repair",
  "location": "Mysuru",
  "description": "Canal maintenance",
  "active": true
}
```
Expected: 201 Created.

### Record attendance
`POST /api/attendance`
```json
{
  "workerId": 1,
  "worksiteId": 1,
  "attendanceDate": "2026-09-21",
  "status": "PRESENT",
  "overtimeHours": 2
}
```
Expected: 201 Created. Use valid IDs returned by your own create calls.

### Weekly payroll
`GET /api/payroll/worker/1/weekly?weekStartDate=2026-09-21&weekEndDate=2026-09-27`
Expected response for the problem's Ravi example:
```json
{
  "workerId": 1,
  "workerName": "Ravi",
  "weekStartDate": "2026-09-21",
  "weekEndDate": "2026-09-27",
  "presentDays": 5,
  "halfDays": 1,
  "absentDays": 1,
  "regularWage": 4400.00,
  "overtimePay": 450.00,
  "totalPayable": 4850.00
}
```

### Record partial payment
`POST /api/payments`
```json
{
  "workerId": 1,
  "weekStartDate": "2026-09-21",
  "weekEndDate": "2026-09-27",
  "amountPaid": 2000,
  "paymentDate": "2026-09-28",
  "remarks": "First instalment"
}
```
Expected: 201 Created, payable amount is derived from attendance and status is `PARTIALLY_PAID`.

### Other endpoint testing
- `GET /api/workers`, `GET /api/workers/1`, `PUT /api/workers/1`, `DELETE /api/workers/1`
- `GET /api/worksites`, `GET /api/worksites/1`, `PUT /api/worksites/1`, `DELETE /api/worksites/1`
- `GET /api/attendance`, `GET /api/attendance/1`, `PUT /api/attendance/1`, `DELETE /api/attendance/1`
- `GET /api/payments`, `GET /api/payments/1`, `PUT /api/payments/1`

### Edge-case checklist
| Test | Example | Expected |
|---|---|---|
| Negative daily wage | Worker `dailyWage: -10` | 400 |
| Blank worker name | `name: ""` | 400 |
| Duplicate worker code | Reuse `W001` | 400 |
| Invalid worker ID | Attendance `workerId: 99999` | 404 |
| Invalid worksite ID | Attendance `worksiteId: 99999` | 404 |
| Negative overtime | `overtimeHours: -1` | 400 |
| ABSENT + overtime | status `ABSENT`, hours `1` | 400 |
| Duplicate attendance | Same worker and date twice | 409 |
| Negative payment | `amountPaid: -1` | 400 |
| Payment exceeds payable | `amountPaid: 999999` | 400 |
| Partial payment | Amount paid less than payable | `PARTIALLY_PAID` |
| Full payment | Amount paid equals payable | `PAID` |
| No payment yet | Amount paid `0` | `PENDING` |
| Invalid date range | End before start | 400 |

## 11. Error response
Example:
```json
{
  "timestamp": "2026-09-28T10:30:00",
  "status": 400,
  "error": "Invalid Request",
  "message": "Absent attendance cannot contain overtime hours.",
  "path": "/api/attendance"
}
```

## 12. Testing strategy
Run `mvn clean test`. The included unit test checks the stated seven-day sample calculation. Then use Postman to test every endpoint with both valid and invalid requests. Verify persistence by checking the four tables in MySQL Workbench. For attendance uniqueness, the service checks first and the database composite unique constraint protects against concurrent duplicate inserts.

## 13. Future enhancements
Role-based login, downloadable payslips, attendance filtering/pagination, audit history, notification on payment, and a simple frontend can be considered after the REST backend is complete.

## 14. Academic documentation
See `docs/ACADEMIC_DOCUMENTATION.md` for requirements, architecture, HLD/LLD, ER/UML/use-case/DFD descriptions, schema, validation, exception handling, and testing strategy.

## 15. Viva
See `docs/VIVA_QUESTIONS_AND_ANSWERS.md` for beginner-friendly viva preparation.
