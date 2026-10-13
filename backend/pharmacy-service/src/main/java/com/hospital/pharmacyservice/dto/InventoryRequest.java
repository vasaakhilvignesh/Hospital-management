package com.hospital.pharmacyservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InventoryRequest {

    @NotNull(message = "Medicine ID is required")
    private Long medicineId;

    @NotBlank(message = "Batch number is required")
    private String batchNumber;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantityAvailable;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel = 10;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    private LocalDate receivedDate = LocalDate.now();

    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be positive")
    private BigDecimal unitPurchasePrice;

    public InventoryRequest() {}

    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }

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
}
