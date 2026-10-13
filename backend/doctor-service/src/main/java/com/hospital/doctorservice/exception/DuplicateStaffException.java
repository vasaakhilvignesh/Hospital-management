package com.hospital.doctorservice.exception;

public class DuplicateStaffException extends RuntimeException {
    public DuplicateStaffException(String message) {
        super(message);
    }
}
