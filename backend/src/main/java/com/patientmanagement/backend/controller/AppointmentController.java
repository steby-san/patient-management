package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.AppointmentCreateDto;
import com.patientmanagement.backend.dto.AppointmentResponseDto;
import com.patientmanagement.backend.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDate(date));
    }

  
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @Valid @RequestBody AppointmentCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentCreateDto dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.updateStatus(id, "CANCELLED");
        return ResponseEntity.noContent().build();
    }
}