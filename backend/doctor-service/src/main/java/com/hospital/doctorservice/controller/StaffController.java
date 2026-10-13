package com.hospital.doctorservice.controller;

import com.hospital.doctorservice.dto.StaffRequest;
import com.hospital.doctorservice.dto.StaffResponse;
import com.hospital.doctorservice.dto.StaffScheduleRequest;
import com.hospital.doctorservice.dto.StaffScheduleResponse;
import com.hospital.doctorservice.service.StaffScheduleService;
import com.hospital.doctorservice.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;
    private final StaffScheduleService scheduleService;

    public StaffController(StaffService staffService, StaffScheduleService scheduleService) {
        this.staffService = staffService;
        this.scheduleService = scheduleService;
    }

    // ================= Staff Management =================

    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(@Valid @RequestBody StaffRequest request) {
        StaffResponse response = staffService.createStaff(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getStaffById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StaffResponse>> searchStaff(@RequestParam("query") String query) {
        return ResponseEntity.ok(staffService.searchStaff(query));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffRequest request) {
        return ResponseEntity.ok(staffService.updateStaff(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    // ================= Staff Schedules =================

    @PostMapping("/{staffId}/schedules")
    public ResponseEntity<StaffScheduleResponse> addSchedule(@PathVariable Long staffId,
                                                             @Valid @RequestBody StaffScheduleRequest request) {
        StaffScheduleResponse response = scheduleService.addSchedule(staffId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{staffId}/schedules")
    public ResponseEntity<List<StaffScheduleResponse>> getSchedules(@PathVariable Long staffId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByStaff(staffId));
    }

    @PutMapping("/{staffId}/schedules/{scheduleId}")
    public ResponseEntity<StaffScheduleResponse> updateSchedule(@PathVariable Long staffId,
                                                                @PathVariable Long scheduleId,
                                                                @Valid @RequestBody StaffScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(staffId, scheduleId, request));
    }

    @DeleteMapping("/{staffId}/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long staffId, @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(staffId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
