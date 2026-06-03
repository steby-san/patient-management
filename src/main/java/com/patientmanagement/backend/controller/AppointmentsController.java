package com.patientmanagement.backend.controller;

import com.patientmanagement.backend.dto.AppointmentsDTO;
import com.patientmanagement.backend.service.AppointmentsService;
import jakarta.persistence.NotNull;
import jakarta.persistence.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/appointments")
public class AppointmentsController {

    @Autowired
    private AppointmentsService appointmentsService;

    @PostMapping
    public String save(@Valid @RequestBody AppointmentsVO vO) {
        return appointmentsService.save(vO).toString();
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @NotNull @PathVariable("id") Long id) {
        appointmentsService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(@Valid @NotNull @PathVariable("id") Long id,
                       @Valid @RequestBody AppointmentsUpdateVO vO) {
        appointmentsService.update(id, vO);
    }

    @GetMapping("/{id}")
    public AppointmentsDTO getById(@Valid @NotNull @PathVariable("id") Long id) {
        return appointmentsService.getById(id);
    }

    @GetMapping
    public Page<AppointmentsDTO> query(@Valid AppointmentsQueryVO vO) {
        return appointmentsService.query(vO);
    }
}
