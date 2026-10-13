package com.hospital.pharmacyservice.controller;

import com.hospital.pharmacyservice.dto.PrescriptionRequest;
import com.hospital.pharmacyservice.dto.PrescriptionResponse;
import com.hospital.pharmacyservice.entity.PrescriptionStatus;
import com.hospital.pharmacyservice.security.UserPrincipal;
import com.hospital.pharmacyservice.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @Valid @RequestBody PrescriptionRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        PrescriptionResponse created = prescriptionService.createPrescription(req, principal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'PHARMACIST', 'PATIENT')")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptions(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) PrescriptionStatus status,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(prescriptionService.getPrescriptions(patientId, doctorId, status, principal));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'PHARMACIST', 'PATIENT')")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id, principal));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'PHARMACIST', 'PATIENT')")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByPatient(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientId(patientId, principal));
    }

    @GetMapping("/my-prescriptions")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<PrescriptionResponse>> getMyPrescriptions(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(prescriptionService.getMyPrescriptions(principal));
    }

    @PatchMapping("/{id}/dispense")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PHARMACIST')")
    public ResponseEntity<PrescriptionResponse> dispensePrescription(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        PrescriptionResponse dispensed = prescriptionService.dispensePrescription(id, principal);
        return ResponseEntity.ok(dispensed);
    }
}
