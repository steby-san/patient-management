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

@jakarta.persistence.Table(name = "patients")
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
@Table(name = "patients")
public class Patients implements Serializable {

    private static final long serialVersionUID = 1L;

    @jakarta.persistence.GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "id", nullable = false)
    @jakarta.persistence.Id
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(name = "patient_code", nullable = false)
    @Column(name = "patient_code", nullable = false)
    private String patientCode;

    @jakarta.persistence.Column(name = "full_name", nullable = false)
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @jakarta.persistence.Column(name = "date_of_birth")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @jakarta.persistence.Column(name = "gender")
    @Column(name = "gender")
    private String gender;

    @jakarta.persistence.Column(name = "phone_number")
    @Column(name = "phone_number")
    private String phoneNumber;

    @jakarta.persistence.Column(name = "email")
    @Column(name = "email")
    private String email;

    @jakarta.persistence.Column(name = "address")
    @Column(name = "address")
    private String address;

    @jakarta.persistence.Column(name = "created_at")
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @jakarta.persistence.Column(name = "deleted", nullable = false)
    @Column(name = "deleted", nullable = false)
    private Integer deleted = 0;

    @jakarta.persistence.Column(name = "deleted_at")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
