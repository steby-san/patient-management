package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.HealthMetricsDTO;
import com.patientmanagement.backend.service.HealthMetricsService;
import jakarta.persistence.NotNull;
import jakarta.persistence.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/healthMetrics")
public class HealthMetricsController {

    @Autowired
    private HealthMetricsService healthMetricsService;

    @PostMapping
    public String save(@Valid @RequestBody HealthMetricsVO vO) {
        return healthMetricsService.save(vO).toString();
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @NotNull @PathVariable("id") Long id) {
        healthMetricsService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(@Valid @NotNull @PathVariable("id") Long id,
                       @Valid @RequestBody HealthMetricsUpdateVO vO) {
        healthMetricsService.update(id, vO);
    }

    @GetMapping("/{id}")
    public HealthMetricsDTO getById(@Valid @NotNull @PathVariable("id") Long id) {
        return healthMetricsService.getById(id);
    }

    @GetMapping
    public Page<HealthMetricsDTO> query(@Valid HealthMetricsQueryVO vO) {
        return healthMetricsService.query(vO);
    }
}
