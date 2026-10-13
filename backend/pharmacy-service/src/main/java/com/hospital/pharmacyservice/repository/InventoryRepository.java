package com.hospital.pharmacyservice.repository;

import com.hospital.pharmacyservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByMedicineId(Long medicineId);

    Optional<Inventory> findByMedicineIdAndBatchNumber(Long medicineId, String batchNumber);

    // FIFO deduction: Active non-expired batches with quantity > 0 sorted by receivedDate ASC
    @Query("SELECT i FROM Inventory i WHERE i.medicine.id = :medicineId AND i.quantityAvailable > 0 AND i.expiryDate >= :today ORDER BY i.receivedDate ASC")
    List<Inventory> findAvailableBatchesForDispense(@Param("medicineId") Long medicineId, @Param("today") LocalDate today);

    // Low stock batches where available <= reorder level
    @Query("SELECT i FROM Inventory i WHERE i.quantityAvailable <= i.reorderLevel")
    List<Inventory> findLowStockBatches();

    // Expiring batches where expiryDate between today and targetDate
    @Query("SELECT i FROM Inventory i WHERE i.expiryDate BETWEEN :today AND :cutoffDate ORDER BY i.expiryDate ASC")
    List<Inventory> findExpiringBatches(@Param("today") LocalDate today, @Param("cutoffDate") LocalDate cutoffDate);
}
