package com.hospital.pharmacyservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false, length = 50)
    private String batchNumber;

    @Column(nullable = false)
    private Integer quantityAvailable;

    @Column(nullable = false)
    private Integer reorderLevel = 10;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private LocalDate receivedDate = LocalDate.now();

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPurchasePrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Inventory() {
    }

    public Inventory(Medicine medicine, String batchNumber, Integer quantityAvailable,
                     Integer reorderLevel, LocalDate expiryDate, LocalDate receivedDate,
                     BigDecimal unitPurchasePrice) {
        this.medicine = medicine;
        this.batchNumber = batchNumber;
        this.quantityAvailable = quantityAvailable;
        this.reorderLevel = reorderLevel != null ? reorderLevel : 10;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate != null ? receivedDate : LocalDate.now();
        this.unitPurchasePrice = unitPurchasePrice;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
