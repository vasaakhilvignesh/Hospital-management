package com.hospital.doctorservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hospital.doctorservice.entity.Shift;
import com.hospital.doctorservice.entity.StaffSchedule;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class StaffScheduleResponse {

    private Long id;
    private Long staffId;
    private DayOfWeek dayOfWeek;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private Shift shift;

    public StaffScheduleResponse() {
    }

    public static StaffScheduleResponse fromEntity(StaffSchedule sched) {
        StaffScheduleResponse resp = new StaffScheduleResponse();
        resp.setId(sched.getId());
        resp.setStaffId(sched.getStaffId());
        resp.setDayOfWeek(sched.getDayOfWeek());
        resp.setStartTime(sched.getStartTime());
        resp.setEndTime(sched.getEndTime());
        resp.setShift(sched.getShift());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Shift getShift() { return shift; }
    public void setShift(Shift shift) { this.shift = shift; }
}
