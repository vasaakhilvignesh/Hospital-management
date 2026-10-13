package com.hospital.pharmacyservice.service;

import com.hospital.pharmacyservice.dto.InventoryRequest;
import com.hospital.pharmacyservice.dto.InventoryResponse;
import com.hospital.pharmacyservice.entity.Inventory;
import com.hospital.pharmacyservice.entity.Medicine;
import com.hospital.pharmacyservice.exception.MedicineNotFoundException;
import com.hospital.pharmacyservice.repository.InventoryRepository;
import com.hospital.pharmacyservice.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MedicineRepository medicineRepository;

    public InventoryService(InventoryRepository inventoryRepository, MedicineRepository medicineRepository) {
        this.inventoryRepository = inventoryRepository;
        this.medicineRepository = medicineRepository;
    }

    public InventoryResponse addStock(InventoryRequest req) {
        Medicine medicine = medicineRepository.findById(req.getMedicineId())
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + req.getMedicineId()));

        Inventory inventory = new Inventory(
                medicine,
                req.getBatchNumber(),
                req.getQuantityAvailable(),
                req.getReorderLevel(),
                req.getExpiryDate(),
                req.getReceivedDate() != null ? req.getReceivedDate() : LocalDate.now(),
                req.getUnitPurchasePrice()
        );

        Inventory saved = inventoryRepository.save(inventory);
        return InventoryResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory(Long medicineId) {
        List<Inventory> list;
        if (medicineId != null) {
            list = inventoryRepository.findByMedicineId(medicineId);
        } else {
            list = inventoryRepository.findAll();
        }
        return list.stream()
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(Long id) {
        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory batch not found with id: " + id));
        return InventoryResponse.fromEntity(inv);
    }

    public InventoryResponse updateInventory(Long id, InventoryRequest req) {
        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory batch not found with id: " + id));

        if (req.getMedicineId() != null && !req.getMedicineId().equals(inv.getMedicine().getId())) {
            Medicine medicine = medicineRepository.findById(req.getMedicineId())
                    .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + req.getMedicineId()));
            inv.setMedicine(medicine);
        }

        inv.setBatchNumber(req.getBatchNumber());
        inv.setQuantityAvailable(req.getQuantityAvailable());
        inv.setReorderLevel(req.getReorderLevel());
        inv.setExpiryDate(req.getExpiryDate());
        if (req.getReceivedDate() != null) {
            inv.setReceivedDate(req.getReceivedDate());
        }
        inv.setUnitPurchasePrice(req.getUnitPurchasePrice());

        return InventoryResponse.fromEntity(inventoryRepository.save(inv));
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockBatches() {
        return inventoryRepository.findLowStockBatches().stream()
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getExpiringBatches(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(daysAhead > 0 ? daysAhead : 30);
        return inventoryRepository.findExpiringBatches(today, cutoff).stream()
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
