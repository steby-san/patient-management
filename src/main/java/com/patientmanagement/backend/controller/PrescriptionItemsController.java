package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.PrescriptionItemsDTO;
import com.patientmanagement.backend.service.PrescriptionItemsService;
import jakarta.persistence.NotNull;
import jakarta.persistence.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/prescriptionItems")
public class PrescriptionItemsController {

    @Autowired
    private PrescriptionItemsService prescriptionItemsService;

    @PostMapping
    public String save(@Valid @RequestBody PrescriptionItemsVO vO) {
        return prescriptionItemsService.save(vO).toString();
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @NotNull @PathVariable("id") Long id) {
        prescriptionItemsService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(@Valid @NotNull @PathVariable("id") Long id,
                       @Valid @RequestBody PrescriptionItemsUpdateVO vO) {
        prescriptionItemsService.update(id, vO);
    }

    @GetMapping("/{id}")
    public PrescriptionItemsDTO getById(@Valid @NotNull @PathVariable("id") Long id) {
        return prescriptionItemsService.getById(id);
    }

    @GetMapping
    public Page<PrescriptionItemsDTO> query(@Valid PrescriptionItemsQueryVO vO) {
        return prescriptionItemsService.query(vO);
    }
}
