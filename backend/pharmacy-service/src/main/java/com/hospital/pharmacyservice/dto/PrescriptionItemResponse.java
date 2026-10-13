package com.hospital.pharmacyservice.dto;

import com.hospital.pharmacyservice.entity.PrescriptionItem;

public class PrescriptionItemResponse {

    private Long id;
    private Long medicineId;
    private String medicineCode;
    private String medicineName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private Integer quantity;
    private String instructions;

    public PrescriptionItemResponse() {}

    public static PrescriptionItemResponse fromEntity(PrescriptionItem item) {
        PrescriptionItemResponse res = new PrescriptionItemResponse();
        res.setId(item.getId());
        if (item.getMedicine() != null) {
            res.setMedicineId(item.getMedicine().getId());
            res.setMedicineCode(item.getMedicine().getMedicineCode());
            res.setMedicineName(item.getMedicine().getName());
        }
        res.setDosage(item.getDosage());
        res.setFrequency(item.getFrequency());
        res.setDurationDays(item.getDurationDays());
        res.setQuantity(item.getQuantity());
        res.setInstructions(item.getInstructions());
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

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
