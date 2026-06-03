package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.FlywaySchemaHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FlywaySchemaHistoryRepository extends JpaRepository<FlywaySchemaHistory, Integer>, JpaSpecificationExecutor<FlywaySchemaHistory> {

}