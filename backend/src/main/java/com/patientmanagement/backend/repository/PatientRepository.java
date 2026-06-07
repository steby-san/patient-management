package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    /**
     * Counts all patients whose patient_code matches the given LIKE pattern,
     * including soft-deleted rows. Must use nativeQuery=true because the
     * @SQLRestriction on Patient entity would otherwise filter out deleted rows in JPQL.
     *
     * @param pattern LIKE pattern, e.g. "BN-2025-%"
     * @return count of matching rows including soft-deleted ones
     */
    @Query(value = "SELECT COUNT(*) FROM patients WHERE patient_code LIKE :pattern", nativeQuery = true)
    long countAllByPatientCodePattern(@Param("pattern") String pattern);
}
