package com.hospital.pharmacyservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String prescriptionNumber;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long doctorId;

    private Long appointmentId;

    @Column(length = 255)
    private String diagnosis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrescriptionStatus status = PrescriptionStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<PrescriptionItem> items = new ArrayList<>();

    private LocalDateTime dispensedAt;

    @Column(length = 100)
    private String dispensedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Prescription() {
    }

    public Prescription(String prescriptionNumber, Long patientId, Long doctorId, Long appointmentId,
                        String diagnosis, String notes) {
        this.prescriptionNumber = prescriptionNumber;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.status = PrescriptionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(PrescriptionItem item) {
        items.add(item);
        item.setPrescription(this);
    }

    public void removeItem(PrescriptionItem item) {
        items.remove(item);
        item.setPrescription(null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPrescriptionNumber() { return prescriptionNumber; }
    public void setPrescriptionNumber(String prescriptionNumber) { this.prescriptionNumber = prescriptionNumber; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public PrescriptionStatus getStatus() { return status; }
    public void setStatus(PrescriptionStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<PrescriptionItem> getItems() { return items; }
    public void setItems(List<PrescriptionItem> items) { this.items = items; }

    public LocalDateTime getDispensedAt() { return dispensedAt; }
    public void setDispensedAt(LocalDateTime dispensedAt) { this.dispensedAt = dispensedAt; }

    public String getDispensedBy() { return dispensedBy; }
    public void setDispensedBy(String dispensedBy) { this.dispensedBy = dispensedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
