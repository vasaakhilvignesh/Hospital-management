package com.hospital.patientservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "immunizations")
public class Immunization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false, length = 100)
    private String vaccineName;

    @Column(nullable = false)
    private LocalDate dateGiven;

    private LocalDate nextDueDate;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Immunization() {
    }

    public Immunization(Long patientId, String vaccineName, LocalDate dateGiven, LocalDate nextDueDate, String notes) {
        this.patientId = patientId;
        this.vaccineName = vaccineName;
        this.dateGiven = dateGiven;
        this.nextDueDate = nextDueDate;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
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
