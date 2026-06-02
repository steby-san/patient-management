package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.HealthMetricInputDto;
import com.patientmanagement.backend.dto.HealthMetricResponseDto;
import com.patientmanagement.backend.entity.HealthMetric;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.HealthMetricRepository;
import com.patientmanagement.backend.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthMetricService {

    private final HealthMetricRepository healthMetricRepository;
    private final PatientRepository patientRepository;

    public HealthMetricService(HealthMetricRepository healthMetricRepository,
                               PatientRepository patientRepository) {
        this.healthMetricRepository = healthMetricRepository;
        this.patientRepository = patientRepository;
    }

    // Task 8.1 — Requirements 14.1, 14.2, 14.3
    public HealthMetricResponseDto addMetric(HealthMetricInputDto dto) {
        var patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with id: " + dto.getPatientId()));

        LocalDateTime measuredAt = dto.getMeasuredAt() != null
                ? dto.getMeasuredAt()
                : LocalDateTime.now();

        HealthMetric metric = HealthMetric.builder()
                .patient(patient)
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .systolic(dto.getSystolic())
                .diastolic(dto.getDiastolic())
                .heartRate(dto.getHeartRate())
                .measuredAt(measuredAt)
                .build();

        HealthMetric saved = healthMetricRepository.save(metric);
        return toResponseDto(saved);
    }

    // Task 8.2 — Requirements 14.4
    public List<HealthMetricResponseDto> getMetricsByPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Patient not found with id: " + patientId));

        return healthMetricRepository.findByPatientIdOrderByMeasuredAtDesc(patientId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // Task 8.2 — Requirements 14.5
    public void deleteMetric(Long id) {
        healthMetricRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Health metric not found with id: " + id));

        healthMetricRepository.deleteById(id);
    }

    private HealthMetricResponseDto toResponseDto(HealthMetric metric) {
        return HealthMetricResponseDto.builder()
                .id(metric.getId())
                .patientId(metric.getPatient().getId())
                .weight(metric.getWeight())
                .height(metric.getHeight())
                .systolic(metric.getSystolic())
                .diastolic(metric.getDiastolic())
                .heartRate(metric.getHeartRate())
                .measuredAt(metric.getMeasuredAt())
                .build();
    }
}
