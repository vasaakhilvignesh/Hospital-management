package com.hospital.patientservice.exception;

public class ImmunizationNotFoundException extends RuntimeException {
    public ImmunizationNotFoundException(String message) {
        super(message);
    }
}
