package com.patientmanagement.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VietnamesePhoneValidator implements ConstraintValidator<VietnamesePhone, String> {
    private static final String REGEX = "^(0|\\+84)(3[2-9]|5[6-9]|7[06-9]|8[0-9]|9[0-9])\\d{7}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // null is valid (field is optional)
        return value.matches(REGEX);
    }
}
