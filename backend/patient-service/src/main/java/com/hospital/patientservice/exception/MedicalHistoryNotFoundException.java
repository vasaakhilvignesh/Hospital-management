package com.hospital.patientservice.exception;

public class MedicalHistoryNotFoundException extends RuntimeException {
    public MedicalHistoryNotFoundException(String message) {
        super(message);
    }
}
