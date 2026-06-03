package com.patientmanagement.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@jakarta.persistence.Table(name = "medications")
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
@Table(name = "medications")
public class Medications implements Serializable {

    private static final long serialVersionUID = 1L;

    @jakarta.persistence.GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "id", nullable = false)
    @jakarta.persistence.Id
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(name = "code", nullable = false)
    @Column(name = "code", nullable = false)
    private String code;

    @jakarta.persistence.Column(name = "name", nullable = false)
    @Column(name = "name", nullable = false)
    private String name;

    @jakarta.persistence.Column(name = "active_ingredient")
    @Column(name = "active_ingredient")
    private String activeIngredient;

    @jakarta.persistence.Column(name = "unit")
    @Column(name = "unit")
    private String unit;

}
