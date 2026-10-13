package com.hospital.pharmacyservice.dto;

import com.hospital.pharmacyservice.entity.Inventory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InventoryResponse {

    private Long id;
    private Long medicineId;
    private String medicineCode;
    private String medicineName;
    private String batchNumber;
    private Integer quantityAvailable;
    private Integer reorderLevel;
    private LocalDate expiryDate;
    private LocalDate receivedDate;
    private BigDecimal unitPurchasePrice;
    private boolean lowStock;
    private boolean expiringSoon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InventoryResponse() {}

    public static InventoryResponse fromEntity(Inventory inv) {
        InventoryResponse res = new InventoryResponse();
        res.setId(inv.getId());
        if (inv.getMedicine() != null) {
            res.setMedicineId(inv.getMedicine().getId());
            res.setMedicineCode(inv.getMedicine().getMedicineCode());
            res.setMedicineName(inv.getMedicine().getName());
        }
        res.setBatchNumber(inv.getBatchNumber());
        res.setQuantityAvailable(inv.getQuantityAvailable());
        res.setReorderLevel(inv.getReorderLevel());
        res.setExpiryDate(inv.getExpiryDate());
        res.setReceivedDate(inv.getReceivedDate());
        res.setUnitPurchasePrice(inv.getUnitPurchasePrice());
        res.setLowStock(inv.getQuantityAvailable() <= inv.getReorderLevel());
        res.setExpiringSoon(inv.getExpiryDate() != null && 
                            inv.getExpiryDate().isBefore(LocalDate.now().plusDays(30)));
        res.setCreatedAt(inv.getCreatedAt());
        res.setUpdatedAt(inv.getUpdatedAt());
        return res;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }

    public String getMedicineCode() { return medicineCode; }
    public void setMedicineCode(String medicineCode) { this.medicineCode = medicineCode; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public BigDecimal getUnitPurchasePrice() { return unitPurchasePrice; }
    public void setUnitPurchasePrice(BigDecimal unitPurchasePrice) { this.unitPurchasePrice = unitPurchasePrice; }

    public boolean isLowStock() { return lowStock; }
    public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }

    public boolean isExpiringSoon() { return expiringSoon; }
    public void setExpiringSoon(boolean expiringSoon) { this.expiringSoon = expiringSoon; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
