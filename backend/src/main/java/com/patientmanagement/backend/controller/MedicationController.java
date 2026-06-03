package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.MedicationRequestDto;
import com.patientmanagement.backend.dto.MedicationResponseDto;
import com.patientmanagement.backend.service.MedicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;


    @PostMapping
    public ResponseEntity<MedicationResponseDto> createMedication(@Valid @RequestBody MedicationRequestDto dto) {
        MedicationResponseDto createdMedication = medicationService.createMedication(dto);
        return new ResponseEntity<>(createdMedication, HttpStatus.CREATED); // 201 Created
    }


    @GetMapping
    public ResponseEntity<List<MedicationResponseDto>> getAllMedications() {
        List<MedicationResponseDto> medications = medicationService.getAllMedications();
        return ResponseEntity.ok(medications); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationResponseDto> getMedicationById(@PathVariable Long id) {
        MedicationResponseDto medication = medicationService.getMedicationById(id);
        return ResponseEntity.ok(medication); // 200 OK
    }


    @PutMapping("/{id}")
    public ResponseEntity<MedicationResponseDto> updateMedication(
            @PathVariable Long id,
            @Valid @RequestBody MedicationRequestDto dto) {

        MedicationResponseDto updatedMedication = medicationService.updateMedication(id, dto);
        return ResponseEntity.ok(updatedMedication); // 200 OK
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedication(@PathVariable Long id) {
        medicationService.deleteMedication(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}