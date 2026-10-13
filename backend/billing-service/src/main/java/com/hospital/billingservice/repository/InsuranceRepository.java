package com.hospital.billingservice.repository;

import com.hospital.billingservice.entity.Insurance;
import com.hospital.billingservice.entity.InsuranceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
    List<Insurance> findByPatientId(Long patientId);
    List<Insurance> findByPatientIdAndStatus(Long patientId, InsuranceStatus status);
    Optional<Insurance> findByPolicyNumber(String policyNumber);
}
