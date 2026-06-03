package com.patientmanagement.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@jakarta.persistence.Table(name = "health_metrics")
@lombok.NoArgsConstructor
@lombok.experimental.SuperBuilder
@lombok.ToString
@lombok.Setter
@lombok.Getter
@jakarta.persistence.Entity
@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "health_metrics")
public class HealthMetrics implements Serializable {

    private static final long serialVersionUID = 1L;

    @jakarta.persistence.GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "id", nullable = false)
    @jakarta.persistence.Id
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(name = "patient_id", nullable = false)
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    /**
     * kg
     */
    @jakarta.persistence.Column(name = "weight")
    @Column(name = "weight")
    private BigDecimal weight;

    /**
     * cm
     */
    @jakarta.persistence.Column(name = "height")
    @Column(name = "height")
    private BigDecimal height;

    /**
     * mmHg
     */
    @jakarta.persistence.Column(name = "systolic")
    @Column(name = "systolic")
    private Integer systolic;

    /**
     * mmHg
     */
    @jakarta.persistence.Column(name = "diastolic")
    @Column(name = "diastolic")
    private Integer diastolic;

    /**
     * bpm
     */
    @jakarta.persistence.Column(name = "heart_rate")
    @Column(name = "heart_rate")
    private Integer heartRate;

    @jakarta.persistence.Column(name = "measured_at")
    @Column(name = "measured_at")
    private LocalDateTime measuredAt = LocalDateTime.now();

}
