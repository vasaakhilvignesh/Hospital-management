package com.hospital.doctorservice.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "staff_schedules")
public class StaffSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long staffId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Shift shift;

    public StaffSchedule() {
    }

    public StaffSchedule(Long staffId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Shift shift) {
        this.staffId = staffId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shift = shift;
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
