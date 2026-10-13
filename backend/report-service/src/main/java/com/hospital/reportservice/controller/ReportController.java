package com.hospital.reportservice.controller;

import com.hospital.reportservice.dto.DashboardSummaryResponse;
import com.hospital.reportservice.dto.ReportRequest;
import com.hospital.reportservice.dto.ReportResponse;
import com.hospital.reportservice.entity.ReportType;
import com.hospital.reportservice.security.UserPrincipal;
import com.hospital.reportservice.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public ResponseEntity<ReportResponse> createReport(
            @Valid @RequestBody ReportRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return new ResponseEntity<>(reportService.createReport(req, principal), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<List<ReportResponse>> getReports(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) ReportType type,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reportService.getReports(patientId, type, principal));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<ReportResponse> getReportById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reportService.getReportById(id, principal));
    }

    @GetMapping("/code/{reportCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<ReportResponse> getReportByCode(
            @PathVariable String reportCode,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reportService.getReportByCode(reportCode, principal));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<List<ReportResponse>> getReportsByPatientId(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reportService.getReportsByPatientId(patientId, principal));
    }

    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ReportResponse> archiveReport(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reportService.archiveReport(id, principal));
    }

    @GetMapping("/dashboard/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'STAFF')")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reportService.getDashboardSummary(principal));
    }
}
