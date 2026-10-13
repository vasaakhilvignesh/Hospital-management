package com.hospital.pharmacyservice.repository;

import com.hospital.pharmacyservice.entity.Medicine;
import com.hospital.pharmacyservice.entity.MedicineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Optional<Medicine> findByMedicineCode(String medicineCode);
    List<Medicine> findByNameContainingIgnoreCaseOrGenericNameContainingIgnoreCase(String name, String genericName);
    List<Medicine> findByStatus(MedicineStatus status);
}
