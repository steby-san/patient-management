package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
}
