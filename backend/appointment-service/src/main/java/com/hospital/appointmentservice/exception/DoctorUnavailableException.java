package com.hospital.appointmentservice.exception;

public class DoctorUnavailableException extends RuntimeException {
    public DoctorUnavailableException(String message) {
        super(message);
    }
}
