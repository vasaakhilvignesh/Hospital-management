package com.hospital.billingservice.repository;

import com.hospital.billingservice.entity.Bill;
import com.hospital.billingservice.entity.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillNumber(String billNumber);
    List<Bill> findByPatientId(Long patientId);
    List<Bill> findByPatientIdAndStatus(Long patientId, BillStatus status);
    List<Bill> findByStatus(BillStatus status);
    List<Bill> findByBillDate(LocalDate billDate);
}
