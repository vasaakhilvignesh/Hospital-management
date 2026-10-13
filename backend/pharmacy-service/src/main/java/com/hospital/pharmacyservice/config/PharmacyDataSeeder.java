package com.hospital.pharmacyservice.config;

import com.hospital.pharmacyservice.entity.Inventory;
import com.hospital.pharmacyservice.entity.Medicine;
import com.hospital.pharmacyservice.entity.MedicineStatus;
import com.hospital.pharmacyservice.repository.InventoryRepository;
import com.hospital.pharmacyservice.repository.MedicineRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class PharmacyDataSeeder implements CommandLineRunner {

    private final MedicineRepository medicineRepository;
    private final InventoryRepository inventoryRepository;

    public PharmacyDataSeeder(MedicineRepository medicineRepository, InventoryRepository inventoryRepository) {
        this.medicineRepository = medicineRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) {
        if (medicineRepository.count() > 0) {
            return;
        }

        // 1. Seed Medicines
        Medicine amox = medicineRepository.save(new Medicine(
                "MED-AMOX-500", "Amoxicillin", "Amoxicillin Trihydrate",
                "Antibiotics", "Pfizer", "Capsule", "500mg",
                new BigDecimal("12.50"), true, MedicineStatus.ACTIVE
        ));

        Medicine para = medicineRepository.save(new Medicine(
                "MED-PARA-650", "Paracetamol", "Acetaminophen",
                "Analgesics", "GSK", "Tablet", "650mg",
                new BigDecimal("3.00"), false, MedicineStatus.ACTIVE
        ));

        Medicine ibu = medicineRepository.save(new Medicine(
                "MED-IBU-400", "Ibuprofen", "Ibuprofen",
                "NSAID", "Abbott", "Tablet", "400mg",
                new BigDecimal("6.20"), false, MedicineStatus.ACTIVE
        ));

        Medicine metf = medicineRepository.save(new Medicine(
                "MED-METF-500", "Metformin", "Metformin Hydrochloride",
                "Antidiabetic", "Merck", "Tablet", "500mg",
                new BigDecimal("8.75"), true, MedicineStatus.ACTIVE
        ));

        Medicine ator = medicineRepository.save(new Medicine(
                "MED-ATOR-20", "Atorvastatin", "Atorvastatin Calcium",
                "Cardiovascular", "Novartis", "Tablet", "20mg",
                new BigDecimal("15.00"), true, MedicineStatus.ACTIVE
        ));

        Medicine azith = medicineRepository.save(new Medicine(
                "MED-AZITH-500", "Azithromycin", "Azithromycin Dihydrate",
                "Antibiotics", "Cipla", "Tablet", "500mg",
                new BigDecimal("22.00"), true, MedicineStatus.ACTIVE
        ));

        Medicine omep = medicineRepository.save(new Medicine(
                "MED-OMEP-20", "Omeprazole", "Omeprazole",
                "Gastrointestinal", "AstraZeneca", "Capsule", "20mg",
                new BigDecimal("9.50"), false, MedicineStatus.ACTIVE
        ));

        // 2. Seed Inventory Batches
        LocalDate today = LocalDate.now();

        // Amoxicillin Batch 1 (older received date, normal stock)
        inventoryRepository.save(new Inventory(
                amox, "BATCH-AMOX-001", 30, 15,
                today.plusMonths(12), today.minusDays(40), new BigDecimal("8.00")
        ));

        // Amoxicillin Batch 2 (newer received date, normal stock - for FIFO verification)
        inventoryRepository.save(new Inventory(
                amox, "BATCH-AMOX-002", 70, 15,
                today.plusMonths(18), today.minusDays(10), new BigDecimal("8.20")
        ));

        // Paracetamol (normal high stock)
        inventoryRepository.save(new Inventory(
                para, "BATCH-PARA-001", 200, 25,
                today.plusMonths(24), today.minusDays(30), new BigDecimal("1.50")
        ));

        // Ibuprofen - LOW STOCK (qty 5 <= reorder level 20)
        inventoryRepository.save(new Inventory(
                ibu, "BATCH-IBU-001", 5, 20,
                today.plusMonths(10), today.minusDays(50), new BigDecimal("3.80")
        ));

        // Metformin - EXPIRING SOON (expires in 12 days)
        inventoryRepository.save(new Inventory(
                metf, "BATCH-METF-EXP", 40, 10,
                today.plusDays(12), today.minusDays(180), new BigDecimal("5.50")
        ));

        // Atorvastatin - Normal stock
        inventoryRepository.save(new Inventory(
                ator, "BATCH-ATOR-001", 60, 15,
                today.plusMonths(14), today.minusDays(15), new BigDecimal("10.00")
        ));

        // Azithromycin - Normal stock
        inventoryRepository.save(new Inventory(
                azith, "BATCH-AZITH-001", 45, 10,
                today.plusMonths(8), today.minusDays(20), new BigDecimal("16.00")
        ));

        // Omeprazole - Normal stock
        inventoryRepository.save(new Inventory(
                omep, "BATCH-OMEP-001", 85, 20,
                today.plusMonths(16), today.minusDays(25), new BigDecimal("6.00")
        ));
    }
}
