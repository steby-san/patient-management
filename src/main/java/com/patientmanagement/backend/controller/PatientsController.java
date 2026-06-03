package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.PatientsDTO;
import com.patientmanagement.backend.service.PatientsService;
import jakarta.persistence.NotNull;
import jakarta.persistence.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/patients")
public class PatientsController {

    @Autowired
    private PatientsService patientsService;

    @PostMapping
    public String save(@Valid @RequestBody PatientsVO vO) {
        return patientsService.save(vO).toString();
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @NotNull @PathVariable("id") Long id) {
        patientsService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(@Valid @NotNull @PathVariable("id") Long id,
                       @Valid @RequestBody PatientsUpdateVO vO) {
        patientsService.update(id, vO);
    }

    @GetMapping("/{id}")
    public PatientsDTO getById(@Valid @NotNull @PathVariable("id") Long id) {
        return patientsService.getById(id);
    }

    @GetMapping
    public Page<PatientsDTO> query(@Valid PatientsQueryVO vO) {
        return patientsService.query(vO);
    }
}
