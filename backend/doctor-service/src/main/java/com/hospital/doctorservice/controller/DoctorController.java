package com.hospital.doctorservice.controller;

import com.hospital.doctorservice.dto.DoctorAvailabilityRequest;
import com.hospital.doctorservice.dto.DoctorAvailabilityResponse;
import com.hospital.doctorservice.dto.DoctorRequest;
import com.hospital.doctorservice.dto.DoctorResponse;
import com.hospital.doctorservice.service.DoctorAvailabilityService;
import com.hospital.doctorservice.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.hospital.doctorservice.security.UserPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorAvailabilityService availabilityService;

    public DoctorController(DoctorService doctorService, DoctorAvailabilityService availabilityService) {
        this.doctorService = doctorService;
        this.availabilityService = availabilityService;
    }

    // ================= Doctor Management =================

    @GetMapping("/me")
    public ResponseEntity<DoctorResponse> getCurrentDoctor(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(doctorService.getDoctorByUserId(principal.getUserId()));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<DoctorResponse> getDoctorByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(doctorService.getDoctorByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorResponse>> searchDoctors(@RequestParam("query") String query) {
        return ResponseEntity.ok(doctorService.searchDoctors(query));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // ================= Doctor Availability =================

    @PostMapping("/{doctorId}/availability")
    public ResponseEntity<DoctorAvailabilityResponse> addAvailability(@PathVariable Long doctorId,
                                                                      @Valid @RequestBody DoctorAvailabilityRequest request) {
        DoctorAvailabilityResponse response = availabilityService.addAvailability(doctorId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<List<DoctorAvailabilityResponse>> getAvailability(@PathVariable Long doctorId) {
        return ResponseEntity.ok(availabilityService.getAvailabilityByDoctor(doctorId));
    }

    @GetMapping("/{doctorId}/availability/check")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @PathVariable Long doctorId,
            @RequestParam("day") DayOfWeek day,
            @RequestParam("time") @DateTimeFormat(pattern = "HH:mm") LocalTime time) {
        return ResponseEntity.ok(availabilityService.checkAvailability(doctorId, day, time));
    }

    @PutMapping("/{doctorId}/availability/{id}")
    public ResponseEntity<DoctorAvailabilityResponse> updateAvailability(
            @PathVariable Long doctorId,
            @PathVariable Long id,
            @Valid @RequestBody DoctorAvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.updateAvailability(doctorId, id, request));
    }

    @DeleteMapping("/{doctorId}/availability/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long doctorId, @PathVariable Long id) {
        availabilityService.deleteAvailability(doctorId, id);
        return ResponseEntity.noContent().build();
    }
}
