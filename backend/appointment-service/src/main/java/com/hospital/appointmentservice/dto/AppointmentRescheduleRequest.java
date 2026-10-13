package com.hospital.appointmentservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRescheduleRequest {

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

    public AppointmentRescheduleRequest() {
    }

    public AppointmentRescheduleRequest(LocalDate appointmentDate, LocalTime startTime, LocalTime endTime) {
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
