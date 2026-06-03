package com.patientmanagement.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AtLeastOneMetricValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneMetric {
    String message() default "At least one metric (weight, height, systolic, diastolic, heartRate) must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
