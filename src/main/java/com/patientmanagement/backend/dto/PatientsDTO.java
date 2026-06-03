package com.patientmanagement.backend.dto;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String patientCode;

    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;

    private String phoneNumber;

    private String email;

    private String address;

    private LocalDateTime createdAt;

    private Integer deleted;

    private LocalDateTime deletedAt;

}
