package com.hospital.billingservice.exception;

public class BillOperationException extends RuntimeException {
    public BillOperationException(String message) {
        super(message);
    }
}
