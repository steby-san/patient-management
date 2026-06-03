package com.patientmanagement.backend.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HealthMetricsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private Long patientId;


    /**
     * kg
     */
    private BigDecimal weight;


    /**
     * cm
     */
    private BigDecimal height;


    /**
     * mmHg
     */
    private Integer systolic;


    /**
     * mmHg
     */
    private Integer diastolic;


    /**
     * bpm
     */
    private Integer heartRate;

    private LocalDateTime measuredAt;

}
