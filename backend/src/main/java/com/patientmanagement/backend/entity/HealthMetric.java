package com.patientmanagement.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;       // kg

    @Column(precision = 5, scale = 2)
    private BigDecimal height;       // cm

    private Integer systolic;        // mmHg

    private Integer diastolic;       // mmHg

    @Column(name = "heart_rate")
    private Integer heartRate;       // bpm

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;
}
