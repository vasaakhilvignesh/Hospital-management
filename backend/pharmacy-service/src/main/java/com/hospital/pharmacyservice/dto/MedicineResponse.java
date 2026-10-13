package com.hospital.pharmacyservice.dto;

import com.hospital.pharmacyservice.entity.Medicine;
import com.hospital.pharmacyservice.entity.MedicineStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MedicineResponse {

    private Long id;
    private String medicineCode;
    private String name;
    private String genericName;
    private String category;
    private String manufacturer;
    private String dosageForm;
    private String strength;
    private BigDecimal unitPrice;
    private boolean prescriptionRequired;
    private MedicineStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MedicineResponse() {}

    public static MedicineResponse fromEntity(Medicine m) {
        MedicineResponse res = new MedicineResponse();
        res.setId(m.getId());
        res.setMedicineCode(m.getMedicineCode());
        res.setName(m.getName());
        res.setGenericName(m.getGenericName());
        res.setCategory(m.getCategory());
        res.setManufacturer(m.getManufacturer());
        res.setDosageForm(m.getDosageForm());
        res.setStrength(m.getStrength());
        res.setUnitPrice(m.getUnitPrice());
        res.setPrescriptionRequired(m.isPrescriptionRequired());
        res.setStatus(m.getStatus());
        res.setCreatedAt(m.getCreatedAt());
        res.setUpdatedAt(m.getUpdatedAt());
        return res;
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
