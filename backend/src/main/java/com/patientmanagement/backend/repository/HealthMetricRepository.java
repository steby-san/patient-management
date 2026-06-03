package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {
    List<HealthMetric> findByPatientIdOrderByMeasuredAtDesc(Long patientId);
}
