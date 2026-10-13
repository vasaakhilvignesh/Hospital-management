package com.hospital.pharmacyservice.repository;

import com.hospital.pharmacyservice.entity.Prescription;
import com.hospital.pharmacyservice.entity.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);
    List<Prescription> findByPatientId(Long patientId);
    List<Prescription> findByDoctorId(Long doctorId);
    List<Prescription> findByStatus(PrescriptionStatus status);
}
