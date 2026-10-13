package com.hospital.appointmentservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.appointmentservice.entity.Appointment;
import com.hospital.appointmentservice.entity.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentResponse {

    private Long id;
    private String appointmentCode;
    private Long patientId;
    private Long doctorId;
    private LocalDate appointmentDate;

    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.hospital.appointmentservice.config.LocalTimeFlexibleDeserializer.class)
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = com.hospital.appointmentservice.config.LocalTimeFlexibleSerializer.class)
    private LocalTime startTime;

    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.hospital.appointmentservice.config.LocalTimeFlexibleDeserializer.class)
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = com.hospital.appointmentservice.config.LocalTimeFlexibleSerializer.class)
    private LocalTime endTime;

    private String reason;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppointmentResponse() {
    }

    public static AppointmentResponse fromEntity(Appointment apt) {
        AppointmentResponse resp = new AppointmentResponse();
        resp.setId(apt.getId());
        resp.setAppointmentCode(apt.getAppointmentCode());
        resp.setPatientId(apt.getPatientId());
        resp.setDoctorId(apt.getDoctorId());
        resp.setAppointmentDate(apt.getAppointmentDate());
        resp.setStartTime(apt.getStartTime());
        resp.setEndTime(apt.getEndTime());
        resp.setReason(apt.getReason());
        resp.setStatus(apt.getStatus());
        resp.setNotes(apt.getNotes());
        resp.setCreatedAt(apt.getCreatedAt());
        resp.setUpdatedAt(apt.getUpdatedAt());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }

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

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
