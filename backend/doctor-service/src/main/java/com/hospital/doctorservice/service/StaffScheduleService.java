package com.hospital.doctorservice.service;

import com.hospital.doctorservice.dto.StaffScheduleRequest;
import com.hospital.doctorservice.dto.StaffScheduleResponse;
import com.hospital.doctorservice.entity.StaffSchedule;
import com.hospital.doctorservice.exception.ScheduleNotFoundException;
import com.hospital.doctorservice.exception.StaffNotFoundException;
import com.hospital.doctorservice.repository.StaffRepository;
import com.hospital.doctorservice.repository.StaffScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffScheduleService {

    private final StaffScheduleRepository scheduleRepository;
    private final StaffRepository staffRepository;

    public StaffScheduleService(StaffScheduleRepository scheduleRepository, StaffRepository staffRepository) {
        this.scheduleRepository = scheduleRepository;
        this.staffRepository = staffRepository;
    }

    @Transactional
    public StaffScheduleResponse addSchedule(Long staffId, StaffScheduleRequest request) {
        if (!staffRepository.existsById(staffId)) {
            throw new StaffNotFoundException("Staff not found with id: " + staffId);
        }

        StaffSchedule sched = new StaffSchedule(
                staffId,
                request.getDayOfWeek(),
                request.getStartTime(),
                request.getEndTime(),
                request.getShift()
        );

        StaffSchedule saved = scheduleRepository.save(sched);
        return StaffScheduleResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<StaffScheduleResponse> getSchedulesByStaff(Long staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new StaffNotFoundException("Staff not found with id: " + staffId);
        }

        return scheduleRepository.findByStaffId(staffId).stream()
                .map(StaffScheduleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public StaffScheduleResponse updateSchedule(Long staffId, Long scheduleId, StaffScheduleRequest request) {
        if (!staffRepository.existsById(staffId)) {
            throw new StaffNotFoundException("Staff not found with id: " + staffId);
        }

        StaffSchedule sched = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Staff schedule not found with id: " + scheduleId));

        sched.setDayOfWeek(request.getDayOfWeek());
        sched.setStartTime(request.getStartTime());
        sched.setEndTime(request.getEndTime());
        sched.setShift(request.getShift());

        StaffSchedule updated = scheduleRepository.save(sched);
        return StaffScheduleResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteSchedule(Long staffId, Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ScheduleNotFoundException("Staff schedule not found with id: " + scheduleId);
        }
        scheduleRepository.deleteById(scheduleId);
    }
}
