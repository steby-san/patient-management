package com.patientmanagement.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@jakarta.persistence.Table(name = "prescriptions")
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
@Table(name = "prescriptions")
public class Prescriptions implements Serializable {

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

    @jakarta.persistence.Column(name = "doctor_name")
    @Column(name = "doctor_name")
    private String doctorName;

    @jakarta.persistence.Column(name = "start_date", nullable = false)
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @jakarta.persistence.Column(name = "end_date", nullable = false)
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @jakarta.persistence.Column(name = "diagnosis")
    @Column(name = "diagnosis")
    private String diagnosis;

    @jakarta.persistence.Column(name = "created_at")
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
