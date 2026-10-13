package com.hospital.pharmacyservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "prescription_items")
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    @JsonBackReference
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false, length = 100)
    private String dosage; // e.g. 500mg

    @Column(nullable = false, length = 100)
    private String frequency; // e.g. Twice daily after meals

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 255)
    private String instructions;

    public PrescriptionItem() {
    }

    public PrescriptionItem(Prescription prescription, Medicine medicine, String dosage,
                            String frequency, Integer durationDays, Integer quantity, String instructions) {
        this.prescription = prescription;
        this.medicine = medicine;
        this.dosage = dosage;
        this.frequency = frequency;
        this.durationDays = durationDays;
        this.quantity = quantity;
        this.instructions = instructions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Prescription getPrescription() { return prescription; }
    public void setPrescription(Prescription prescription) { this.prescription = prescription; }

    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }

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
