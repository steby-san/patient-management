package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.HealthMetricInputDto;
import com.patientmanagement.backend.dto.HealthMetricResponseDto;
import com.patientmanagement.backend.entity.HealthMetric;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.HealthMetricRepository;
import com.patientmanagement.backend.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthMetricService {

    private final HealthMetricRepository healthMetricRepository;
    private final PatientRepository patientRepository;

    public HealthMetricService(HealthMetricRepository healthMetricRepository,
                               PatientRepository patientRepository) {
        this.healthMetricRepository = healthMetricRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public HealthMetricResponseDto addMetric(HealthMetricInputDto dto) {

        if (dto.getSystolic() != null && dto.getDiastolic() != null && dto.getSystolic() < dto.getDiastolic()) {
            throw new IllegalArgumentException("Systolic pressure cannot be lower than diastolic pressure");
        }

        var patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + dto.getPatientId()));

        LocalDateTime measuredAt = dto.getMeasuredAt() != null ? dto.getMeasuredAt() : LocalDateTime.now();

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


    @Transactional(readOnly = true)
    public List<HealthMetricResponseDto> getMetricsByPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));

        return healthMetricRepository.findByPatientIdOrderByMeasuredAtDesc(patientId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }


    @Transactional
    public void deleteMetric(Long id) {
        HealthMetric metric = healthMetricRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Health metric not found with id: " + id));

        // Dùng delete(entity) thay vì deleteById(id) để tránh lỗi Extra SQL query khi không tìm thấy ID
        healthMetricRepository.delete(metric);
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

    // ===================================================================
    // Lấy chi tiết 1 bản ghi chỉ số sức khỏe
    // ===================================================================
    @Transactional(readOnly = true)
    public HealthMetricResponseDto getMetricById(Long id) {
        HealthMetric metric = healthMetricRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Health metric not found with id: " + id));
        return toResponseDto(metric);
    }


    @Transactional
    public HealthMetricResponseDto updateMetric(Long id, HealthMetricInputDto dto) {

        HealthMetric metric = healthMetricRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Health metric not found with id: " + id));


        if (dto.getWeight() != null && dto.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        if (dto.getHeight() != null && dto.getHeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Height must be greater than 0");
        }

        if (dto.getSystolic() != null && dto.getDiastolic() != null && dto.getSystolic() < dto.getDiastolic()) {
            throw new IllegalArgumentException("Systolic pressure cannot be lower than diastolic pressure");
        }


        if (dto.getWeight() != null) metric.setWeight(dto.getWeight());
        if (dto.getHeight() != null) metric.setHeight(dto.getHeight());
        if (dto.getSystolic() != null) metric.setSystolic(dto.getSystolic());
        if (dto.getDiastolic() != null) metric.setDiastolic(dto.getDiastolic());
        if (dto.getHeartRate() != null) metric.setHeartRate(dto.getHeartRate());
        if (dto.getMeasuredAt() != null) metric.setMeasuredAt(dto.getMeasuredAt());


        return toResponseDto(healthMetricRepository.save(metric));
    }
}