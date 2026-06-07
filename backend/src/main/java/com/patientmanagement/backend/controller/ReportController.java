package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.TopBmiPatientDto;
import com.patientmanagement.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/top-bmi")
    public List<TopBmiPatientDto> getTopBmiPatients(
            @RequestParam(defaultValue = "10") int limit) {

        return reportService.getTopBmiPatients(limit);
    }

    @GetMapping("/high-blood-pressure-rate")
    public Map<String, Object> getHighBloodPressureRate() {
        return reportService.getHighBpRatio();
    }

    @GetMapping("/active-medications")
    public List<Map<String, Object>> getActiveMedications() {
        return reportService.getActiveMedications();
    }
}