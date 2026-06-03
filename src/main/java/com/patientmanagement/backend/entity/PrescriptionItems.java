package com.patientmanagement.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@jakarta.persistence.Table(name = "prescription_items")
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
@Table(name = "prescription_items")
public class PrescriptionItems implements Serializable {

    private static final long serialVersionUID = 1L;

    @jakarta.persistence.GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "id", nullable = false)
    @jakarta.persistence.Id
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(name = "prescription_id", nullable = false)
    @Column(name = "prescription_id", nullable = false)
    private Long prescriptionId;

    @jakarta.persistence.Column(name = "medication_id", nullable = false)
    @Column(name = "medication_id", nullable = false)
    private Long medicationId;

    /**
     * VD: Uống 1 viên sau ăn
     */
    @jakarta.persistence.Column(name = "dosage")
    @Column(name = "dosage")
    private String dosage;

    @jakarta.persistence.Column(name = "quantity", nullable = false)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

}
