package com.hospital.patientservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ImmunizationRequest {

    @NotBlank(message = "Vaccine name is required")
    private String vaccineName;

    @NotNull(message = "Date given is required")
    private LocalDate dateGiven;

    private LocalDate nextDueDate;
    private String notes;

    public ImmunizationRequest() {
    }

    public ImmunizationRequest(String vaccineName, LocalDate dateGiven, LocalDate nextDueDate, String notes) {
        this.vaccineName = vaccineName;
        this.dateGiven = dateGiven;
        this.nextDueDate = nextDueDate;
        this.notes = notes;
    }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public LocalDate getDateGiven() { return dateGiven; }
    public void setDateGiven(LocalDate dateGiven) { this.dateGiven = dateGiven; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
