package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.TopBmiPatientDto;
import com.patientmanagement.backend.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/top-bmi")
    public ResponseEntity<List<TopBmiPatientDto>> getTopBmi(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reportService.getTopBmiPatients(limit));
    }

    @GetMapping("/high-bp-ratio")
    public ResponseEntity<Map<String, Object>> getHighBpRatio() {
        return ResponseEntity.ok(reportService.getHighBpRatio());
    }

    @GetMapping("/active-medications")
    public ResponseEntity<List<Map<String, Object>>> getActiveMedications() {
        return ResponseEntity.ok(reportService.getActiveMedications());
    }
}
