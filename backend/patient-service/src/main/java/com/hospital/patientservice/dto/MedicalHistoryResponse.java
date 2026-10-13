package com.hospital.patientservice.dto;

import com.hospital.patientservice.entity.MedicalHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicalHistoryResponse {

    private Long id;
    private Long patientId;
    private String diagnosis;
    private String treatment;
    private String medications;
    private String notes;
    private LocalDate recordDate;
    private String doctorReference;
    private LocalDateTime createdAt;

    public MedicalHistoryResponse() {
    }

    public static MedicalHistoryResponse fromEntity(MedicalHistory history) {
        MedicalHistoryResponse resp = new MedicalHistoryResponse();
        resp.setId(history.getId());
        resp.setPatientId(history.getPatientId());
        resp.setDiagnosis(history.getDiagnosis());
        resp.setTreatment(history.getTreatment());
        resp.setMedications(history.getMedications());
        resp.setNotes(history.getNotes());
        resp.setRecordDate(history.getRecordDate());
        resp.setDoctorReference(history.getDoctorReference());
        resp.setCreatedAt(history.getCreatedAt());
        return resp;
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
