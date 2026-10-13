package com.hospital.appointmentservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hospital.appointmentservice.config.LocalTimeFlexibleDeserializer;
import com.hospital.appointmentservice.config.LocalTimeFlexibleSerializer;

import java.time.LocalTime;

public class SlotResponse {

    @JsonDeserialize(using = LocalTimeFlexibleDeserializer.class)
    @JsonSerialize(using = LocalTimeFlexibleSerializer.class)
    private LocalTime startTime;

    @JsonDeserialize(using = LocalTimeFlexibleDeserializer.class)
    @JsonSerialize(using = LocalTimeFlexibleSerializer.class)
    private LocalTime endTime;

    private boolean available;

    public SlotResponse() {
    }

    public SlotResponse(LocalTime startTime, LocalTime endTime, boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
