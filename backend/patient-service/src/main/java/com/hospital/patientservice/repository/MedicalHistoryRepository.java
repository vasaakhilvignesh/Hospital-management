package com.hospital.patientservice.repository;

import com.hospital.patientservice.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPatientIdOrderByRecordDateDesc(Long patientId);
}
