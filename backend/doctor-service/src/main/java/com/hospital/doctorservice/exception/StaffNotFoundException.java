package com.hospital.doctorservice.exception;

public class StaffNotFoundException extends RuntimeException {
    public StaffNotFoundException(String message) {
        super(message);
    }
}
