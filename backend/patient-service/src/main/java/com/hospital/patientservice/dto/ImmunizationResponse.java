package com.hospital.patientservice.dto;

import com.hospital.patientservice.entity.Immunization;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ImmunizationResponse {

    private Long id;
    private Long patientId;
    private String vaccineName;
    private LocalDate dateGiven;
    private LocalDate nextDueDate;
    private String notes;
    private LocalDateTime createdAt;

    public ImmunizationResponse() {
    }

    public static ImmunizationResponse fromEntity(Immunization imm) {
        ImmunizationResponse resp = new ImmunizationResponse();
        resp.setId(imm.getId());
        resp.setPatientId(imm.getPatientId());
        resp.setVaccineName(imm.getVaccineName());
        resp.setDateGiven(imm.getDateGiven());
        resp.setNextDueDate(imm.getNextDueDate());
        resp.setNotes(imm.getNotes());
        resp.setCreatedAt(imm.getCreatedAt());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public LocalDate getDateGiven() { return dateGiven; }
    public void setDateGiven(LocalDate dateGiven) { this.dateGiven = dateGiven; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
