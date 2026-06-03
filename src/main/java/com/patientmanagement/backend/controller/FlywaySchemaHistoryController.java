package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.FlywaySchemaHistoryDTO;
import com.patientmanagement.backend.service.FlywaySchemaHistoryService;
import jakarta.persistence.NotNull;
import jakarta.persistence.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/flywaySchemaHistory")
public class FlywaySchemaHistoryController {

    @Autowired
    private FlywaySchemaHistoryService flywaySchemaHistoryService;

    @PostMapping
    public String save(@Valid @RequestBody FlywaySchemaHistoryVO vO) {
        return flywaySchemaHistoryService.save(vO).toString();
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @NotNull @PathVariable("id") Integer id) {
        flywaySchemaHistoryService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(@Valid @NotNull @PathVariable("id") Integer id,
                       @Valid @RequestBody FlywaySchemaHistoryUpdateVO vO) {
        flywaySchemaHistoryService.update(id, vO);
    }

    @GetMapping("/{id}")
    public FlywaySchemaHistoryDTO getById(@Valid @NotNull @PathVariable("id") Integer id) {
        return flywaySchemaHistoryService.getById(id);
    }

    @GetMapping
    public Page<FlywaySchemaHistoryDTO> query(@Valid FlywaySchemaHistoryQueryVO vO) {
        return flywaySchemaHistoryService.query(vO);
    }
}
