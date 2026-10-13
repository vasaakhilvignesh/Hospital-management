package com.hospital.patientservice.dto;

import com.hospital.patientservice.entity.MedicalHistory;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicalHistoryRequest {

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String treatment;
    private String medications;
    private String notes;
    private LocalDate recordDate;
    private String doctorReference;

    public MedicalHistoryRequest() {
    }

    public MedicalHistoryRequest(String diagnosis, String treatment, String medications, String notes,
                                 LocalDate recordDate, String doctorReference) {
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.medications = medications;
        this.notes = notes;
        this.recordDate = recordDate;
        this.doctorReference = doctorReference;
    }

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
}
