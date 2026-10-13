package com.hospital.pharmacyservice.dto;

import com.hospital.pharmacyservice.entity.MedicineStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MedicineRequest {

    @NotBlank(message = "Medicine code is required")
    private String medicineCode;

    @NotBlank(message = "Medicine name is required")
    private String name;

    @NotBlank(message = "Generic name is required")
    private String genericName;

    @NotBlank(message = "Category is required")
    private String category;

    private String manufacturer;
    private String dosageForm;
    private String strength;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than zero")
    private BigDecimal unitPrice;

    private boolean prescriptionRequired = true;
    private MedicineStatus status = MedicineStatus.ACTIVE;

    public MedicineRequest() {}

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
}
