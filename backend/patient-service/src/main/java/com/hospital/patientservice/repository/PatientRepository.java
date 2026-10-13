package com.hospital.patientservice.repository;

import com.hospital.patientservice.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientCode(String patientCode);
    Optional<Patient> findByUserId(Long userId);
    boolean existsByPatientCode(String patientCode);
    boolean existsByEmail(String email);

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.patientCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.phone LIKE CONCAT('%', :query, '%')")
    List<Patient> searchPatients(@Param("query") String query);
}
