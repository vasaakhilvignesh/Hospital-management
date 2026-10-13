package com.hospital.pharmacyservice.controller;

import com.hospital.pharmacyservice.dto.InventoryRequest;
import com.hospital.pharmacyservice.dto.InventoryResponse;
import com.hospital.pharmacyservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PHARMACIST')")
    public ResponseEntity<InventoryResponse> addStock(@Valid @RequestBody InventoryRequest req) {
        InventoryResponse created = inventoryService.addStock(req);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PHARMACIST', 'DOCTOR')")
    public ResponseEntity<List<InventoryResponse>> getAllInventory(
            @RequestParam(required = false) Long medicineId) {
        return ResponseEntity.ok(inventoryService.getAllInventory(medicineId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PHARMACIST', 'DOCTOR')")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PHARMACIST')")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequest req) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, req));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PHARMACIST')")
    public ResponseEntity<List<InventoryResponse>> getLowStockBatches() {
        return ResponseEntity.ok(inventoryService.getLowStockBatches());
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PHARMACIST')")
    public ResponseEntity<List<InventoryResponse>> getExpiringBatches(
            @RequestParam(defaultValue = "30") int daysAhead) {
        return ResponseEntity.ok(inventoryService.getExpiringBatches(daysAhead));
    }
}
