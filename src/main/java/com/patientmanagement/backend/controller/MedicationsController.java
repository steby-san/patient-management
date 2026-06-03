package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.MedicationsDTO;
import com.patientmanagement.backend.service.MedicationsService;
import jakarta.persistence.NotNull;
import jakarta.persistence.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/medications")
public class MedicationsController {

    @Autowired
    private MedicationsService medicationsService;

    @PostMapping
    public String save(@Valid @RequestBody MedicationsVO vO) {
        return medicationsService.save(vO).toString();
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @NotNull @PathVariable("id") Long id) {
        medicationsService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(@Valid @NotNull @PathVariable("id") Long id,
                       @Valid @RequestBody MedicationsUpdateVO vO) {
        medicationsService.update(id, vO);
    }

    @GetMapping("/{id}")
    public MedicationsDTO getById(@Valid @NotNull @PathVariable("id") Long id) {
        return medicationsService.getById(id);
    }

    @GetMapping
    public Page<MedicationsDTO> query(@Valid MedicationsQueryVO vO) {
        return medicationsService.query(vO);
    }
}
