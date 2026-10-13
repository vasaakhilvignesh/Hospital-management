package com.hospital.doctorservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.doctorservice.entity.Shift;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class StaffScheduleRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "Shift is required")
    private Shift shift;

    public StaffScheduleRequest() {
    }

    public StaffScheduleRequest(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Shift shift) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shift = shift;
    }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Shift getShift() { return shift; }
    public void setShift(Shift shift) { this.shift = shift; }
}
