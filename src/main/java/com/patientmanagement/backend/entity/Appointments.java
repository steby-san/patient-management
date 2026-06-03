package com.patientmanagement.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@jakarta.persistence.Table(name = "appointments")
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
@Table(name = "appointments")
public class Appointments implements Serializable {

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

    @jakarta.persistence.Column(name = "appointment_time", nullable = false)
    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;

    /**
     * PENDING, CONFIRMED, COMPLETED, CANCELLED
     */
    @jakarta.persistence.Column(name = "status", nullable = false)
    @Column(name = "status", nullable = false)
    private String status;

    @jakarta.persistence.Column(name = "reason")
    @Column(name = "reason")
    private String reason;

    @jakarta.persistence.Column(name = "doctor_name")
    @Column(name = "doctor_name")
    private String doctorName;

}
