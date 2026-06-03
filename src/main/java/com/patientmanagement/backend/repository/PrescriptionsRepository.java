package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.Prescriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrescriptionsRepository extends JpaRepository<Prescriptions, Long>, JpaSpecificationExecutor<Prescriptions> {

}