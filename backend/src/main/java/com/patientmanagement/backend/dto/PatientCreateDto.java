package com.patientmanagement.backend.dto;

import com.patientmanagement.backend.validation.VietnamesePhone;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientCreateDto {

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @Past
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @VietnamesePhone
    private String phoneNumber;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Size(max = 500)
    private String address;
}
