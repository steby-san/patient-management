package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.HealthMetricInputDto;
import com.patientmanagement.backend.dto.HealthMetricResponseDto;
import com.patientmanagement.backend.service.HealthMetricService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/health-metrics")
@RequiredArgsConstructor
public class HealthMetricController {

    private final HealthMetricService healthMetricService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<HealthMetricResponseDto>> getMetricsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(healthMetricService.getMetricsByPatient(patientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthMetricResponseDto> getMetricById(@PathVariable Long id) {
        return ResponseEntity.ok(healthMetricService.getMetricById(id));
    }

    @PostMapping
    public ResponseEntity<HealthMetricResponseDto> addMetric(@Valid @RequestBody HealthMetricInputDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(healthMetricService.addMetric(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthMetricResponseDto> updateMetric(
            @PathVariable Long id,
            @Valid @RequestBody HealthMetricInputDto dto) {
        return ResponseEntity.ok(healthMetricService.updateMetric(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetric(@PathVariable Long id) {
        healthMetricService.deleteMetric(id);
        return ResponseEntity.noContent().build();
    }
}