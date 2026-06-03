package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.PrescriptionCreateDto;
import com.patientmanagement.backend.dto.PrescriptionResponseDto;
import com.patientmanagement.backend.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionResponseDto> createPrescription(
            @Valid @RequestBody PrescriptionCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.createPrescription(dto));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponseDto>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getByPatient(patientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDto> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getDetail(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDto> updatePrescription(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionCreateDto dto) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
}