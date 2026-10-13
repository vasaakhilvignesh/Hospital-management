package com.hospital.doctorservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.doctorservice.entity.DoctorAvailability;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class DoctorAvailabilityResponse {

    private Long id;
    private Long doctorId;
    private DayOfWeek dayOfWeek;

    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.hospital.doctorservice.config.LocalTimeFlexibleDeserializer.class)
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = com.hospital.doctorservice.config.LocalTimeFlexibleSerializer.class)
    private LocalTime startTime;

    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.hospital.doctorservice.config.LocalTimeFlexibleDeserializer.class)
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = com.hospital.doctorservice.config.LocalTimeFlexibleSerializer.class)
    private LocalTime endTime;

    private boolean available;

    public DoctorAvailabilityResponse() {
    }

    public static DoctorAvailabilityResponse fromEntity(DoctorAvailability av) {
        DoctorAvailabilityResponse resp = new DoctorAvailabilityResponse();
        resp.setId(av.getId());
        resp.setDoctorId(av.getDoctorId());
        resp.setDayOfWeek(av.getDayOfWeek());
        resp.setStartTime(av.getStartTime());
        resp.setEndTime(av.getEndTime());
        resp.setAvailable(av.isAvailable());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
