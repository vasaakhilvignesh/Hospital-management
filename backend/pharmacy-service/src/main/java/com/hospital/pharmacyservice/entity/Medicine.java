package com.hospital.pharmacyservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String medicineCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String genericName;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 100)
    private String manufacturer;

    @Column(length = 50)
    private String dosageForm; // Tablet, Syrup, Injection, Capsule

    @Column(length = 50)
    private String strength; // 500mg, 10ml, etc.

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private boolean prescriptionRequired = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedicineStatus status = MedicineStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Medicine() {
    }

    public Medicine(String medicineCode, String name, String genericName, String category,
                    String manufacturer, String dosageForm, String strength, BigDecimal unitPrice,
                    boolean prescriptionRequired, MedicineStatus status) {
        this.medicineCode = medicineCode;
        this.name = name;
        this.genericName = genericName;
        this.category = category;
        this.manufacturer = manufacturer;
        this.dosageForm = dosageForm;
        this.strength = strength;
        this.unitPrice = unitPrice;
        this.prescriptionRequired = prescriptionRequired;
        this.status = status != null ? status : MedicineStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMedicineCode() { return medicineCode; }
    public void setMedicineCode(String medicineCode) { this.medicineCode = medicineCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }

    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public void setPrescriptionRequired(boolean prescriptionRequired) { this.prescriptionRequired = prescriptionRequired; }

    public MedicineStatus getStatus() { return status; }
    public void setStatus(MedicineStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
