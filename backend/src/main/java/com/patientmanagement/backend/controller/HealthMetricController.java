package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.HealthMetricInputDto;
import com.patientmanagement.backend.dto.HealthMetricResponseDto;
import com.patientmanagement.backend.service.HealthMetricService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/health-metrics")
public class HealthMetricController {

    private final HealthMetricService healthMetricService;

    public HealthMetricController(HealthMetricService healthMetricService) {
        this.healthMetricService = healthMetricService;
    }

    // Requirements 9.1, 9.2, 9.3
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<HealthMetricResponseDto>> getMetricsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(healthMetricService.getMetricsByPatient(patientId));
    }

    // Requirements 10.1, 10.3, 10.4
    @PostMapping
    public ResponseEntity<HealthMetricResponseDto> addMetric(@Valid @RequestBody HealthMetricInputDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(healthMetricService.addMetric(dto));
    }

    // Requirements 11.1, 11.2, 11.3
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetric(@PathVariable Long id) {
        healthMetricService.deleteMetric(id);
        return ResponseEntity.noContent().build();
    }
}
