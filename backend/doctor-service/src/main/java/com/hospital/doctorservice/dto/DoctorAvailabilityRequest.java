package com.hospital.doctorservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class DoctorAvailabilityRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.hospital.doctorservice.config.LocalTimeFlexibleDeserializer.class)
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = com.hospital.doctorservice.config.LocalTimeFlexibleSerializer.class)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.hospital.doctorservice.config.LocalTimeFlexibleDeserializer.class)
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = com.hospital.doctorservice.config.LocalTimeFlexibleSerializer.class)
    private LocalTime endTime;

    private boolean available = true;

    public DoctorAvailabilityRequest() {
    }

    public DoctorAvailabilityRequest(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, boolean available) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
