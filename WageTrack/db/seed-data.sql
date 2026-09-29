-- OPTIONAL DEMO DATA: run only against a fresh/empty wagetrack_db.
-- Hibernate creates the tables when the application starts for the first time.
USE wagetrack_db;

INSERT INTO workers (id, worker_code, name, phone, address, daily_wage, active, created_at) VALUES
(1, 'W001', 'Ravi Kumar', '9876543210', 'Mysuru, Karnataka', 800.00, TRUE, NOW()),
(2, 'W002', 'Meena Devi', '9876543211', 'Mandya, Karnataka', 700.00, TRUE, NOW()),
(3, 'W003', 'Arun Raj', '9876543212', 'Hassan, Karnataka', 900.00, TRUE, NOW());

INSERT INTO worksites (id, site_code, site_name, location, description, active) VALUES
(1, 'S001', 'Canal Repair', 'Mysuru', 'Canal maintenance work', TRUE),
(2, 'S002', 'Farm Harvest', 'Mandya', 'Seasonal farm labour', TRUE);

-- Ravi's 7-day example: expected regular wage 4400, overtime 450, total 4850.
INSERT INTO attendance (id, worker_id, worksite_id, attendance_date, status, overtime_hours, created_at) VALUES
(1, 1, 1, '2026-09-21', 'PRESENT', 2.00, NOW()),
(2, 1, 1, '2026-09-22', 'PRESENT', 0.00, NOW()),
(3, 1, 1, '2026-09-23', 'HALF_DAY', 0.00, NOW()),
(4, 1, 1, '2026-09-24', 'ABSENT', 0.00, NOW()),
(5, 1, 1, '2026-09-25', 'PRESENT', 1.00, NOW()),
(6, 1, 1, '2026-09-26', 'PRESENT', 0.00, NOW()),
(7, 1, 1, '2026-09-27', 'PRESENT', 0.00, NOW()),
(8, 2, 2, '2026-09-21', 'PRESENT', 0.50, NOW()),
(9, 2, 2, '2026-09-22', 'HALF_DAY', 0.00, NOW()),
(10, 2, 2, '2026-09-23', 'PRESENT', 0.00, NOW()),
(11, 3, 1, '2026-09-21', 'PRESENT', 1.00, NOW()),
(12, 3, 1, '2026-09-22', 'ABSENT', 0.00, NOW());

INSERT INTO payment_records
(id, worker_id, week_start_date, week_end_date, payable_amount, amount_paid, payment_date, payment_status, remarks) VALUES
(1, 1, '2026-09-21', '2026-09-27', 4850.00, 2000.00, '2026-09-28', 'PARTIALLY_PAID', 'First instalment'),
(2, 2, '2026-09-21', '2026-09-27', 1815.63, 0.00, NULL, 'PENDING', 'Awaiting settlement');
