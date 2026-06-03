package com.patientmanagement.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreateDto {
    @NotNull
    private Long patientId;

    @NotNull
    @Future
    private LocalDateTime appointmentTime;

    private String reason;
    private String doctorName;
}
