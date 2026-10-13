package com.hospital.pharmacyservice.exception;

public class MedicineNotFoundException extends RuntimeException {
    public MedicineNotFoundException(String message) {
        super(message);
    }
}
