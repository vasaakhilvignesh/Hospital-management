package com.hospital.patientservice.repository;

import com.hospital.patientservice.entity.Immunization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImmunizationRepository extends JpaRepository<Immunization, Long> {
    List<Immunization> findByPatientIdOrderByDateGivenDesc(Long patientId);
}
