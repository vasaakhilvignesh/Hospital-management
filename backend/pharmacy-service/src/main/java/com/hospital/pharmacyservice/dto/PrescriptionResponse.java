package com.hospital.pharmacyservice.dto;

import com.hospital.pharmacyservice.entity.Prescription;
import com.hospital.pharmacyservice.entity.PrescriptionStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PrescriptionResponse {

    private Long id;
    private String prescriptionNumber;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private String diagnosis;
    private PrescriptionStatus status;
    private String notes;
    private List<PrescriptionItemResponse> items;
    private LocalDateTime dispensedAt;
    private String dispensedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PrescriptionResponse() {}

    public static PrescriptionResponse fromEntity(Prescription p) {
        PrescriptionResponse res = new PrescriptionResponse();
        res.setId(p.getId());
        res.setPrescriptionNumber(p.getPrescriptionNumber());
        res.setPatientId(p.getPatientId());
        res.setDoctorId(p.getDoctorId());
        res.setAppointmentId(p.getAppointmentId());
        res.setDiagnosis(p.getDiagnosis());
        res.setStatus(p.getStatus());
        res.setNotes(p.getNotes());
        res.setDispensedAt(p.getDispensedAt());
        res.setDispensedBy(p.getDispensedBy());
        res.setCreatedAt(p.getCreatedAt());
        res.setUpdatedAt(p.getUpdatedAt());

        if (p.getItems() != null) {
            res.setItems(p.getItems().stream()
                    .map(PrescriptionItemResponse::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            res.setItems(Collections.emptyList());
        }
        return res;
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

    public List<PrescriptionItemResponse> getItems() { return items; }
    public void setItems(List<PrescriptionItemResponse> items) { this.items = items; }

    public LocalDateTime getDispensedAt() { return dispensedAt; }
    public void setDispensedAt(LocalDateTime dispensedAt) { this.dispensedAt = dispensedAt; }

    public String getDispensedBy() { return dispensedBy; }
    public void setDispensedBy(String dispensedBy) { this.dispensedBy = dispensedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
