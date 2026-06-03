package com.patientmanagement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponseDto {
    private Long id;
    private Long patientId;
    private String patientName;
    private String doctorName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String diagnosis;
    private LocalDateTime createdAt;
    private List<PrescriptionItemDto> items;
}
