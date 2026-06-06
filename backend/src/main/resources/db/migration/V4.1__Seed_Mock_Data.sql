-- Seed Bệnh nhân (Patients)
INSERT INTO patients (patient_code, full_name, date_of_birth, gender, phone_number, address, deleted) VALUES 
('PT-1001', 'Nguyễn Văn An', '1985-04-12', 'MALE', '0901234567', 'Quận 1, TP HCM', false),
('PT-1002', 'Trần Thị Bình', '1990-08-25', 'FEMALE', '0912345678', 'Quận 3, TP HCM', false),
('PT-1003', 'Lê Hoàng Cường', '1975-11-03', 'MALE', '0987654321', 'Quận Gò Vấp, TP HCM', false),
('PT-1004', 'Phạm Ngọc Dung', '1988-02-15', 'FEMALE', '0909876543', 'Quận 10, TP HCM', false),
('PT-1005', 'Hoàng Bảo Sơn', '2000-05-20', 'MALE', '0933445566', 'Quận Bình Thạnh, TP HCM', false);

-- Seed Chỉ số sức khỏe (Health Metrics)
-- Cho PT-1001
INSERT INTO health_metrics (patient_id, measured_at, height, weight, systolic, diastolic, heart_rate) VALUES 
(1, DATE_SUB(NOW(), INTERVAL 30 DAY), 170.5, 65.0, 120, 80, 75),
(1, NOW(), 170.5, 64.5, 122, 82, 72);

-- Cho PT-1002
INSERT INTO health_metrics (patient_id, measured_at, height, weight, systolic, diastolic, heart_rate) VALUES 
(2, NOW(), 158.0, 52.0, 110, 70, 80);

-- Cho PT-1003 (Huyết áp cao)
INSERT INTO health_metrics (patient_id, measured_at, height, weight, systolic, diastolic, heart_rate) VALUES 
(3, NOW(), 165.0, 75.0, 145, 95, 85);

-- Cho PT-1004 (BMI cao)
INSERT INTO health_metrics (patient_id, measured_at, height, weight, systolic, diastolic, heart_rate) VALUES 
(4, NOW(), 160.0, 80.0, 130, 85, 78);

-- Seed Danh mục Thuốc (Medications)
INSERT INTO medications (code, name, active_ingredient, unit) VALUES 
('MED-001', 'Paracetamol 500mg', 'Paracetamol', 'Viên'),
('MED-002', 'Amoxicillin 500mg', 'Amoxicillin', 'Viên'),
('MED-003', 'Omeprazole 20mg', 'Omeprazole', 'Viên'),
('MED-004', 'Vitamin C 1000mg', 'Ascorbic Acid', 'Viên sủi'),
('MED-005', 'Amlodipine 5mg', 'Amlodipine', 'Viên');

-- Seed Cuộc hẹn (Appointments)
INSERT INTO appointments (patient_id, appointment_time, reason, status, doctor_name) VALUES 
(1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Tái khám định kỳ', 'SCHEDULED', 'BS. Nguyễn Văn A'),
(2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Khám tổng quát', 'SCHEDULED', 'BS. Trần Thị B'),
(3, NOW(), 'Kiểm tra huyết áp', 'COMPLETED', 'BS. Nguyễn Văn A'),
(4, DATE_ADD(NOW(), INTERVAL 5 DAY), 'Tư vấn giảm cân', 'SCHEDULED', 'BS. Lê Hoàng C');

-- Seed Đơn thuốc (Prescriptions)
INSERT INTO prescriptions (patient_id, doctor_name, start_date, end_date, diagnosis) VALUES 
(3, 'BS. Nguyễn Văn A', DATE(NOW()), DATE_ADD(DATE(NOW()), INTERVAL 7 DAY), 'Tăng huyết áp vô căn');

-- Chi tiết đơn thuốc (Prescription Items)
INSERT INTO prescription_items (prescription_id, medication_id, quantity, dosage) VALUES 
(1, 5, 30, '1 viên/ngày vào buổi sáng'),
(1, 4, 15, '1 viên/ngày sau ăn sáng');
