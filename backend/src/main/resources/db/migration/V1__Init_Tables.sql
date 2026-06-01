CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_code VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE health_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    weight DECIMAL(5,2) COMMENT 'kg',
    height DECIMAL(5,2) COMMENT 'cm',
    systolic INT COMMENT 'mmHg',
    diastolic INT COMMENT 'mmHg',
    heart_rate INT COMMENT 'bpm',
    measured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hm_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    appointment_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL COMMENT 'PENDING, CONFIRMED, COMPLETED, CANCELLED',
    reason TEXT,
    doctor_name VARCHAR(100),
    CONSTRAINT fk_app_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_name VARCHAR(100),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    diagnosis TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pres_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE medications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    active_ingredient VARCHAR(255),
    unit VARCHAR(50)
);

CREATE TABLE prescription_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medication_id BIGINT NOT NULL,
    dosage VARCHAR(255) COMMENT 'VD: Uống 1 viên sau ăn',
    quantity INT NOT NULL,
    CONSTRAINT fk_pi_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE,
    CONSTRAINT fk_pi_medication FOREIGN KEY (medication_id) REFERENCES medications(id) ON DELETE CASCADE
);
CREATE INDEX idx_health_metrics_patient_measured ON health_metrics(patient_id, measured_at DESC);
CREATE INDEX idx_health_metrics_measured_at ON health_metrics(measured_at);
CREATE INDEX idx_prescriptions_dates ON prescriptions(start_date, end_date);
