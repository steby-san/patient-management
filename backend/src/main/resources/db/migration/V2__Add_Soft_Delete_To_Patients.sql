-- V2__Add_Soft_Delete_To_Patients.sql
-- Thêm các cột soft delete vào bảng patients (Requirements 1.1, 1.3)
ALTER TABLE patients
    ADD COLUMN deleted     BOOLEAN   NOT NULL DEFAULT FALSE,
    ADD COLUMN deleted_at  TIMESTAMP NULL;

CREATE INDEX idx_patients_deleted ON patients(deleted);
