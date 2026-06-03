package com.patientmanagement.backend.validation;

import com.patientmanagement.backend.dto.HealthMetricInputDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneMetricValidator implements ConstraintValidator<AtLeastOneMetric, HealthMetricInputDto> {

    @Override
    public boolean isValid(HealthMetricInputDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true;
        return dto.getWeight() != null
                || dto.getHeight() != null
                || dto.getSystolic() != null
                || dto.getDiastolic() != null
                || dto.getHeartRate() != null;
    }
}
