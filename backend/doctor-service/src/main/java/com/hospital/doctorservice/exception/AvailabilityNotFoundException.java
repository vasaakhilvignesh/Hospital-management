package com.hospital.doctorservice.exception;

public class AvailabilityNotFoundException extends RuntimeException {
    public AvailabilityNotFoundException(String message) {
        super(message);
    }
}
