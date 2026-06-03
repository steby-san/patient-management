package com.patientmanagement.backend.exception;

public class DuplicatePatientCodeException extends RuntimeException {
    public DuplicatePatientCodeException(String message) {
        super(message);
    }
}
