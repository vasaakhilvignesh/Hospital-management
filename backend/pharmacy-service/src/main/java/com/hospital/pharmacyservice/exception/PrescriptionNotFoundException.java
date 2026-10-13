package com.hospital.pharmacyservice.exception;

public class PrescriptionNotFoundException extends RuntimeException {
    public PrescriptionNotFoundException(String message) {
        super(message);
    }
}
