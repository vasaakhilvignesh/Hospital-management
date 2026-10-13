package com.hospital.pharmacyservice.controller;

import com.hospital.pharmacyservice.dto.MedicineRequest;
import com.hospital.pharmacyservice.dto.MedicineResponse;
import com.hospital.pharmacyservice.entity.MedicineStatus;
import com.hospital.pharmacyservice.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PHARMACIST')")
    public ResponseEntity<MedicineResponse> createMedicine(@Valid @RequestBody MedicineRequest req) {
        MedicineResponse created = medicineService.createMedicine(req);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'PHARMACIST', 'PATIENT')")
    public ResponseEntity<List<MedicineResponse>> getAllMedicines(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MedicineStatus status) {
        return ResponseEntity.ok(medicineService.getAllMedicines(search, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'PHARMACIST', 'PATIENT')")
    public ResponseEntity<MedicineResponse> getMedicineById(@PathVariable Long id) {
        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'PHARMACIST', 'PATIENT')")
    public ResponseEntity<MedicineResponse> getMedicineByCode(@PathVariable String code) {
        return ResponseEntity.ok(medicineService.getMedicineByCode(code));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PHARMACIST')")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequest req) {
        return ResponseEntity.ok(medicineService.updateMedicine(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }
}
