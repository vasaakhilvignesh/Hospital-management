package com.hospital.billingservice.controller;

import com.hospital.billingservice.dto.BillRequest;
import com.hospital.billingservice.dto.BillResponse;
import com.hospital.billingservice.entity.BillStatus;
import com.hospital.billingservice.security.UserPrincipal;
import com.hospital.billingservice.service.BillService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<BillResponse> createBill(
            @Valid @RequestBody BillRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        BillResponse created = billService.createBill(req, principal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<List<BillResponse>> getBills(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) BillStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(billService.getBills(patientId, status, date, principal));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<BillResponse> getBillById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(billService.getBillById(id, principal));
    }

    @GetMapping("/number/{billNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<BillResponse> getBillByNumber(
            @PathVariable String billNumber,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(billService.getBillByNumber(billNumber, principal));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<List<BillResponse>> getBillsByPatientId(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(billService.getBillsByPatientId(patientId, principal));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<BillResponse> updateBill(
            @PathVariable Long id,
            @Valid @RequestBody BillRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(billService.updateBill(id, req, principal));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<BillResponse> cancelBill(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(billService.cancelBill(id, principal));
    }
}
