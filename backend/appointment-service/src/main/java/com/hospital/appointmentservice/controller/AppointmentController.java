package com.hospital.appointmentservice.controller;

import com.hospital.appointmentservice.dto.AppointmentRequest;
import com.hospital.appointmentservice.dto.AppointmentRescheduleRequest;
import com.hospital.appointmentservice.dto.AppointmentResponse;
import com.hospital.appointmentservice.dto.SlotResponse;
import com.hospital.appointmentservice.entity.AppointmentStatus;
import com.hospital.appointmentservice.security.UserPrincipal;
import com.hospital.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentResponse response = appointmentService.createAppointment(request, principal);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAppointments(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AppointmentResponse> response = appointmentService.getAppointments(patientId, doctorId, status, date, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentResponse response = appointmentService.getAppointmentById(id, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{appointmentCode}")
    public ResponseEntity<AppointmentResponse> getAppointmentByCode(
            @PathVariable String appointmentCode,
            @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentResponse response = appointmentService.getAppointmentByCode(appointmentCode, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatient(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AppointmentResponse> response = appointmentService.getAppointmentsByPatient(patientId, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(
            @PathVariable Long doctorId,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AppointmentResponse> response = appointmentService.getAppointmentsByDoctor(doctorId, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SlotResponse> response = appointmentService.getAvailableSlots(doctorId, date, principal);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{id}/reschedule", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRescheduleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentResponse response = appointmentService.rescheduleAppointment(id, request, principal);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{id}/cancel", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentResponse response = appointmentService.cancelAppointment(id, principal);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/{id}/status", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            @RequestParam(required = false) AppointmentStatus status,
            @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentStatus targetStatus = status;
        if (targetStatus == null && body != null) {
            String val = body.get("status");
            if (val == null) {
                val = body.get("appointmentStatus");
            }
            if (val != null && !val.trim().isEmpty()) {
                targetStatus = AppointmentStatus.valueOf(val.trim().toUpperCase());
            }
        }
        if (targetStatus == null) {
            throw new IllegalArgumentException("Appointment status is required either in request body or as 'status' query parameter");
        }
        AppointmentResponse response = appointmentService.updateStatus(id, targetStatus, principal);
        return ResponseEntity.ok(response);
    }
}
