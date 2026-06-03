package com.patientmanagement.backend.dto;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PrescriptionsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private Long patientId;

    private String doctorName;

    private LocalDate startDate;

    private LocalDate endDate;

    private String diagnosis;

    private LocalDateTime createdAt;

}
