package com.hospital.patientservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_histories")
public class MedicalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false, length = 255)
    private String diagnosis;

    @Column(length = 1000)
    private String treatment;

    @Column(length = 500)
    private String medications;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDate recordDate = LocalDate.now();

    @Column(length = 100)
    private String doctorReference;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MedicalHistory() {
    }

    public MedicalHistory(Long patientId, String diagnosis, String treatment, String medications,
                          String notes, LocalDate recordDate, String doctorReference) {
        this.patientId = patientId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.medications = medications;
        this.notes = notes;
        this.recordDate = (recordDate != null) ? recordDate : LocalDate.now();
        this.doctorReference = doctorReference;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getMedications() { return medications; }
    public void setMedications(String medications) { this.medications = medications; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public String getDoctorReference() { return doctorReference; }
    public void setDoctorReference(String doctorReference) { this.doctorReference = doctorReference; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
