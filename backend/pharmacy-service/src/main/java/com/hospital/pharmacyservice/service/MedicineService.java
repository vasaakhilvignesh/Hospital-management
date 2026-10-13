package com.hospital.pharmacyservice.service;

import com.hospital.pharmacyservice.dto.MedicineRequest;
import com.hospital.pharmacyservice.dto.MedicineResponse;
import com.hospital.pharmacyservice.entity.Medicine;
import com.hospital.pharmacyservice.entity.MedicineStatus;
import com.hospital.pharmacyservice.exception.MedicineNotFoundException;
import com.hospital.pharmacyservice.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public MedicineResponse createMedicine(MedicineRequest req) {
        Medicine medicine = new Medicine(
                req.getMedicineCode(),
                req.getName(),
                req.getGenericName(),
                req.getCategory(),
                req.getManufacturer(),
                req.getDosageForm(),
                req.getStrength(),
                req.getUnitPrice(),
                req.isPrescriptionRequired(),
                req.getStatus()
        );
        Medicine saved = medicineRepository.save(medicine);
        return MedicineResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public MedicineResponse getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));
        return MedicineResponse.fromEntity(medicine);
    }

    @Transactional(readOnly = true)
    public MedicineResponse getMedicineByCode(String code) {
        Medicine medicine = medicineRepository.findByMedicineCode(code)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with code: " + code));
        return MedicineResponse.fromEntity(medicine);
    }

    @Transactional(readOnly = true)
    public List<MedicineResponse> getAllMedicines(String search, MedicineStatus status) {
        List<Medicine> medicines;
        if (search != null && !search.trim().isEmpty()) {
            medicines = medicineRepository.findByNameContainingIgnoreCaseOrGenericNameContainingIgnoreCase(search.trim(), search.trim());
        } else if (status != null) {
            medicines = medicineRepository.findByStatus(status);
        } else {
            medicines = medicineRepository.findAll();
        }
        return medicines.stream()
                .map(MedicineResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public MedicineResponse updateMedicine(Long id, MedicineRequest req) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));

        medicine.setMedicineCode(req.getMedicineCode());
        medicine.setName(req.getName());
        medicine.setGenericName(req.getGenericName());
        medicine.setCategory(req.getCategory());
        medicine.setManufacturer(req.getManufacturer());
        medicine.setDosageForm(req.getDosageForm());
        medicine.setStrength(req.getStrength());
        medicine.setUnitPrice(req.getUnitPrice());
        medicine.setPrescriptionRequired(req.isPrescriptionRequired());
        medicine.setStatus(req.getStatus() != null ? req.getStatus() : medicine.getStatus());

        return MedicineResponse.fromEntity(medicineRepository.save(medicine));
    }

    public void deleteMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));
        medicine.setStatus(MedicineStatus.INACTIVE);
        medicineRepository.save(medicine);
    }
}
