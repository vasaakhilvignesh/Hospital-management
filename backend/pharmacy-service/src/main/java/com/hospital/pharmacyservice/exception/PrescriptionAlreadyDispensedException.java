package com.hospital.pharmacyservice.exception;

public class PrescriptionAlreadyDispensedException extends RuntimeException {
    public PrescriptionAlreadyDispensedException(String message) {
        super(message);
    }
}
