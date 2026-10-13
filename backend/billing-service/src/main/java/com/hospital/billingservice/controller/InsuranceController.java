package com.hospital.billingservice.controller;

import com.hospital.billingservice.dto.InsuranceRequest;
import com.hospital.billingservice.dto.InsuranceResponse;
import com.hospital.billingservice.security.UserPrincipal;
import com.hospital.billingservice.service.InsuranceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {

    private final InsuranceService insuranceService;

    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<InsuranceResponse> createInsurance(
            @Valid @RequestBody InsuranceRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        InsuranceResponse created = insuranceService.createInsurance(req, principal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<InsuranceResponse> getInsuranceById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(insuranceService.getInsuranceById(id, principal));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<List<InsuranceResponse>> getInsuranceByPatientId(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(insuranceService.getInsuranceByPatientId(patientId, principal));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<InsuranceResponse> updateInsurance(
            @PathVariable Long id,
            @Valid @RequestBody InsuranceRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(insuranceService.updateInsurance(id, req, principal));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<InsuranceResponse> deactivateInsurance(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(insuranceService.deactivateInsurance(id, principal));
    }
}
