package com.hospital.billingservice.controller;

import com.hospital.billingservice.dto.PaymentRequest;
import com.hospital.billingservice.dto.PaymentResponse;
import com.hospital.billingservice.security.UserPrincipal;
import com.hospital.billingservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<PaymentResponse> recordPayment(
            @Valid @RequestBody PaymentRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        PaymentResponse response = paymentService.recordPayment(req, principal);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'STAFF', 'PATIENT')")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(paymentService.getPaymentById(id, principal));
    }

    @GetMapping("/bill/{billId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'STAFF', 'PATIENT')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByBillId(
            @PathVariable Long billId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(paymentService.getPaymentsByBillId(billId, principal));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(paymentService.refundPayment(id, principal));
    }
}
