package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.PageResponse;
import com.patientmanagement.backend.dto.PatientCreateDto;
import com.patientmanagement.backend.dto.PatientResponseDto;
import com.patientmanagement.backend.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * GET /api/v1/patients
     * Returns a paginated (and optionally searched) list of non-deleted patients.
     * Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6
     */
    @GetMapping
    public ResponseEntity<PageResponse<PatientResponseDto>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        return ResponseEntity.ok(patientService.getAllPatients(page, size, search));
    }

    /**
     * GET /api/v1/patients/{id}
     * Returns the detail of a single non-deleted patient.
     * Requirements: 5.1, 5.2, 5.3
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatient(id));
    }

    /**
     * POST /api/v1/patients
     * Creates a new patient and returns 201 with the created resource.
     * Requirements: 6.1, 6.3
     */
    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(
            @Valid @RequestBody PatientCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(dto));
    }

    /**
     * PUT /api/v1/patients/{id}
     * Updates an existing patient's mutable fields.
     * Requirements: 7.1, 7.2, 7.3
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientCreateDto dto) {
        return ResponseEntity.ok(patientService.updatePatient(id, dto));
    }

    /**
     * DELETE /api/v1/patients/{id}
     * Soft-deletes a patient and returns 204 No Content.
     * Requirements: 8.1, 8.2
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
