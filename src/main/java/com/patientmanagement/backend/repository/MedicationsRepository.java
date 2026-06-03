package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.Medications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MedicationsRepository extends JpaRepository<Medications, Long>, JpaSpecificationExecutor<Medications> {

}