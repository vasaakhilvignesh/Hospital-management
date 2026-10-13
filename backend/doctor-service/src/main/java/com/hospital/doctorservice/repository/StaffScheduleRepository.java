package com.hospital.doctorservice.repository;

import com.hospital.doctorservice.entity.StaffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffScheduleRepository extends JpaRepository<StaffSchedule, Long> {
    List<StaffSchedule> findByStaffId(Long staffId);
}
