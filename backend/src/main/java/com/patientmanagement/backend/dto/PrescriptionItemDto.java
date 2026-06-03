package com.patientmanagement.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItemDto {
    @NotNull
    private Long medicationId;
    
    private String medicationName;
    
    private String dosage;
    
    @Min(1)
    private int quantity;
}
