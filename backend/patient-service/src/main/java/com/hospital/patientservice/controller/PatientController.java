package com.hospital.patientservice.controller;

import com.hospital.patientservice.dto.*;
import com.hospital.patientservice.security.UserPrincipal;
import com.hospital.patientservice.service.ImmunizationService;
import com.hospital.patientservice.service.MedicalHistoryService;
import com.hospital.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final MedicalHistoryService medicalHistoryService;
    private final ImmunizationService immunizationService;

    public PatientController(PatientService patientService,
                             MedicalHistoryService medicalHistoryService,
                             ImmunizationService immunizationService) {
        this.patientService = patientService;
        this.medicalHistoryService = medicalHistoryService;
        this.immunizationService = immunizationService;
    }

    // ================= Patient Endpoints =================

    @PostMapping("/register")
    public ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        PatientResponse response = patientService.registerPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.createPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<PatientResponse> getCurrentPatient(@AuthenticationPrincipal UserPrincipal principal) {
        PatientResponse response = patientService.getPatientByUserId(principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<PatientResponse> getPatientByUserId(@PathVariable Long userId) {
        PatientResponse response = patientService.getPatientByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients(@AuthenticationPrincipal UserPrincipal principal) {
        List<PatientResponse> response = patientService.getAllPatients(principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserPrincipal principal) {
        PatientResponse response = patientService.getPatientById(id, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{patientCode}")
    public ResponseEntity<PatientResponse> getPatientByCode(@PathVariable String patientCode,
                                                            @AuthenticationPrincipal UserPrincipal principal) {
        PatientResponse response = patientService.getPatientByCode(patientCode, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientResponse>> searchPatients(@RequestParam("query") String query,
                                                                @AuthenticationPrincipal UserPrincipal principal) {
        List<PatientResponse> response = patientService.searchPatients(query, principal);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id,
                                                         @Valid @RequestBody PatientRequest request,
                                                         @AuthenticationPrincipal UserPrincipal principal) {
        PatientResponse response = patientService.updatePatient(id, request, principal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    // ================= Medical History Endpoints =================

    @PostMapping("/{patientId}/history")
    public ResponseEntity<MedicalHistoryResponse> addMedicalHistory(@PathVariable Long patientId,
                                                                    @Valid @RequestBody MedicalHistoryRequest request,
                                                                    @AuthenticationPrincipal UserPrincipal principal) {
        MedicalHistoryResponse response = medicalHistoryService.addMedicalHistory(patientId, request, principal);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{patientId}/history")
    public ResponseEntity<List<MedicalHistoryResponse>> getMedicalHistory(@PathVariable Long patientId,
                                                                          @AuthenticationPrincipal UserPrincipal principal) {
        List<MedicalHistoryResponse> response = medicalHistoryService.getMedicalHistoryByPatient(patientId, principal);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{patientId}/history/{historyId}")
    public ResponseEntity<MedicalHistoryResponse> updateMedicalHistory(@PathVariable Long patientId,
                                                                       @PathVariable Long historyId,
                                                                       @Valid @RequestBody MedicalHistoryRequest request,
                                                                       @AuthenticationPrincipal UserPrincipal principal) {
        MedicalHistoryResponse response = medicalHistoryService.updateMedicalHistory(patientId, historyId, request, principal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{patientId}/history/{historyId}")
    public ResponseEntity<Void> deleteMedicalHistory(@PathVariable Long patientId,
                                                     @PathVariable Long historyId,
                                                     @AuthenticationPrincipal UserPrincipal principal) {
        medicalHistoryService.deleteMedicalHistory(patientId, historyId, principal);
        return ResponseEntity.noContent().build();
    }

    // ================= Immunization Endpoints =================

    @PostMapping("/{patientId}/immunizations")
    public ResponseEntity<ImmunizationResponse> addImmunization(@PathVariable Long patientId,
                                                                @Valid @RequestBody ImmunizationRequest request,
                                                                @AuthenticationPrincipal UserPrincipal principal) {
        ImmunizationResponse response = immunizationService.addImmunization(patientId, request, principal);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{patientId}/immunizations")
    public ResponseEntity<List<ImmunizationResponse>> getImmunizations(@PathVariable Long patientId,
                                                                       @AuthenticationPrincipal UserPrincipal principal) {
        List<ImmunizationResponse> response = immunizationService.getImmunizationsByPatient(patientId, principal);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{patientId}/immunizations/{id}")
    public ResponseEntity<ImmunizationResponse> updateImmunization(@PathVariable Long patientId,
                                                                   @PathVariable Long id,
                                                                   @Valid @RequestBody ImmunizationRequest request,
                                                                   @AuthenticationPrincipal UserPrincipal principal) {
        ImmunizationResponse response = immunizationService.updateImmunization(patientId, id, request, principal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{patientId}/immunizations/{id}")
    public ResponseEntity<Void> deleteImmunization(@PathVariable Long patientId,
                                                   @PathVariable Long id,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        immunizationService.deleteImmunization(patientId, id, principal);
        return ResponseEntity.noContent().build();
    }
}
