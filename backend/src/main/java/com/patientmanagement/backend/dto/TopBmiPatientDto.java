package com.patientmanagement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopBmiPatientDto {
    private String patientCode;
    private String fullName;
    private Double weight;
    private Double height;
    private Double bmi;
    private LocalDateTime measuredAt;
}
