package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.PrescriptionsDTO;
import com.patientmanagement.backend.service.PrescriptionsService;
import jakarta.persistence.NotNull;
import jakarta.persistence.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/prescriptions")
public class PrescriptionsController {

    @Autowired
    private PrescriptionsService prescriptionsService;

    @PostMapping
    public String save(@Valid @RequestBody PrescriptionsVO vO) {
        return prescriptionsService.save(vO).toString();
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @NotNull @PathVariable("id") Long id) {
        prescriptionsService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(@Valid @NotNull @PathVariable("id") Long id,
                       @Valid @RequestBody PrescriptionsUpdateVO vO) {
        prescriptionsService.update(id, vO);
    }

    @GetMapping("/{id}")
    public PrescriptionsDTO getById(@Valid @NotNull @PathVariable("id") Long id) {
        return prescriptionsService.getById(id);
    }

    @GetMapping
    public Page<PrescriptionsDTO> query(@Valid PrescriptionsQueryVO vO) {
        return prescriptionsService.query(vO);
    }
}
