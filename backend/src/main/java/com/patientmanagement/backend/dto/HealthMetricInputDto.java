package com.patientmanagement.backend.dto;

import com.patientmanagement.backend.validation.AtLeastOneMetric;
import jakarta.validation.constraints.*;
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
@AtLeastOneMetric
public class HealthMetricInputDto {

    @NotNull
    private Long patientId;

    @DecimalMin("1.0")
    @DecimalMax("500.0")
    private BigDecimal weight;

    @DecimalMin("10.0")
    @DecimalMax("300.0")
    private BigDecimal height;

    @Min(50)
    @Max(300)
    private Integer systolic;

    @Min(30)
    @Max(200)
    private Integer diastolic;

    @Min(20)
    @Max(300)
    private Integer heartRate;

    private LocalDateTime measuredAt;
}
