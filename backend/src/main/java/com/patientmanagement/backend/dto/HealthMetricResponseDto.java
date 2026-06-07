package com.patientmanagement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthMetricResponseDto {

    private Long id;
    private Long patientId;
    private BigDecimal weight;
    private BigDecimal height;
    private Integer systolic;
    private Integer diastolic;
    private Integer heartRate;
    private LocalDateTime measuredAt;
}
