package com.hospital.appointmentservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date must be today or in the future")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.hospital.appointmentservice.config.LocalTimeFlexibleDeserializer.class)
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = com.hospital.appointmentservice.config.LocalTimeFlexibleSerializer.class)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.hospital.appointmentservice.config.LocalTimeFlexibleDeserializer.class)
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = com.hospital.appointmentservice.config.LocalTimeFlexibleSerializer.class)
    private LocalTime endTime;

    private String reason;
    private String notes;

    public AppointmentRequest() {
    }

    public AppointmentRequest(Long patientId, Long doctorId, LocalDate appointmentDate,
                              LocalTime startTime, LocalTime endTime, String reason, String notes) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.notes = notes;
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
