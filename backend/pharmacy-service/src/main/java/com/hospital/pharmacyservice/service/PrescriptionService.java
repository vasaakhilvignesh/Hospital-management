package com.hospital.pharmacyservice.service;

import com.hospital.pharmacyservice.client.DoctorServiceClient;
import com.hospital.pharmacyservice.client.PatientServiceClient;
import com.hospital.pharmacyservice.dto.*;
import com.hospital.pharmacyservice.entity.*;
import com.hospital.pharmacyservice.exception.*;
import com.hospital.pharmacyservice.repository.InventoryRepository;
import com.hospital.pharmacyservice.repository.MedicineRepository;
import com.hospital.pharmacyservice.repository.PrescriptionRepository;
import com.hospital.pharmacyservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final InventoryRepository inventoryRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               MedicineRepository medicineRepository,
                               InventoryRepository inventoryRepository,
                               PatientServiceClient patientServiceClient,
                               DoctorServiceClient doctorServiceClient) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicineRepository = medicineRepository;
        this.inventoryRepository = inventoryRepository;
        this.patientServiceClient = patientServiceClient;
        this.doctorServiceClient = doctorServiceClient;
    }

    public PrescriptionResponse createPrescription(PrescriptionRequest req, UserPrincipal principal) {
        Long effectiveDoctorId = req.getDoctorId();
        if (principal != null && "DOCTOR".equalsIgnoreCase(principal.getRole())) {
            try {
                java.util.Map<String, Object> doc = doctorServiceClient.getDoctorByUserId(principal.getUserId(), principal.getRawToken());
                if (doc != null && doc.get("id") != null) {
                    effectiveDoctorId = Long.valueOf(doc.get("id").toString());
                }
            } catch (Exception ignored) {
                // Fall back to req.getDoctorId() if doctor resolution fails
            }
        }

        // Validate patient and doctor across services if token available
        if (principal != null && principal.getRawToken() != null) {
            patientServiceClient.getPatient(req.getPatientId(), principal.getRawToken());
            doctorServiceClient.getDoctor(effectiveDoctorId, principal.getRawToken());
        }

        String rxNumber = "RX-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        Prescription prescription = new Prescription(
                rxNumber,
                req.getPatientId(),
                effectiveDoctorId,
                req.getAppointmentId(),
                req.getDiagnosis(),
                req.getNotes()
        );

        for (PrescriptionItemRequest itemReq : req.getItems()) {
            Medicine medicine = medicineRepository.findById(itemReq.getMedicineId())
                    .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + itemReq.getMedicineId()));

            PrescriptionItem item = new PrescriptionItem(
                    prescription,
                    medicine,
                    itemReq.getDosage(),
                    itemReq.getFrequency(),
                    itemReq.getDurationDays(),
                    itemReq.getQuantity(),
                    itemReq.getInstructions()
            );
            prescription.addItem(item);
        }

        Prescription saved = prescriptionRepository.save(prescription);
        return PrescriptionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id, UserPrincipal principal) {
        Prescription rx = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + id));

        // Patient isolation
        if (principal != null && "PATIENT".equalsIgnoreCase(principal.getRole())) {
            try {
                java.util.Map<String, Object> patient = patientServiceClient.getPatient(rx.getPatientId(), principal.getRawToken());
                Object uid = patient.get("userId");
                if (uid == null || !uid.toString().equals(principal.getUserId().toString())) {
                    throw new AccessDeniedException("Access denied: You can only view your own prescriptions");
                }
            } catch (AccessDeniedException e) {
                throw e;
            } catch (Exception e) {
                throw new AccessDeniedException("Access denied: You can only view your own prescriptions");
            }
        }

        return PrescriptionResponse.fromEntity(rx);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByPatientId(Long patientId, UserPrincipal principal) {
        // Enforce patient isolation
        if (principal != null && "PATIENT".equalsIgnoreCase(principal.getRole())) {
            Long selfPatientId = null;
            try {
                java.util.Map<String, Object> pat = patientServiceClient.getPatientByUserId(principal.getUserId(), principal.getRawToken());
                if (pat != null && pat.get("id") != null) {
                    selfPatientId = Long.valueOf(pat.get("id").toString());
                }
            } catch (Exception e) {
                throw new AccessDeniedException("Access denied: You can only view your own prescriptions");
            }
            if (selfPatientId == null || !patientId.equals(selfPatientId)) {
                throw new AccessDeniedException("Access denied: You cannot view another patient's prescriptions");
            }
        }

        List<Prescription> list = prescriptionRepository.findByPatientId(patientId);
        return list.stream()
                .map(PrescriptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getMyPrescriptions(UserPrincipal principal) {
        if (principal == null || principal.getUserId() == null) {
            throw new AccessDeniedException("Access denied: Authentication required");
        }
        Long selfPatientId = null;
        try {
            java.util.Map<String, Object> pat = patientServiceClient.getPatientByUserId(principal.getUserId(), principal.getRawToken());
            if (pat != null && pat.get("id") != null) {
                selfPatientId = Long.valueOf(pat.get("id").toString());
            }
        } catch (Exception e) {
            throw new AccessDeniedException("Access denied: Patient profile not found");
        }
        if (selfPatientId == null) {
            return List.of();
        }
        return prescriptionRepository.findByPatientId(selfPatientId).stream()
                .map(PrescriptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptions(Long patientId, Long doctorId, PrescriptionStatus status, UserPrincipal principal) {
        // Enforce patient isolation
        if (principal != null && "PATIENT".equalsIgnoreCase(principal.getRole())) {
            Long selfPatientId = null;
            try {
                java.util.Map<String, Object> pat = patientServiceClient.getPatientByUserId(principal.getUserId(), principal.getRawToken());
                if (pat != null && pat.get("id") != null) {
                    selfPatientId = Long.valueOf(pat.get("id").toString());
                }
            } catch (Exception e) {
                return List.of();
            }
            if (patientId != null && !patientId.equals(selfPatientId)) {
                throw new AccessDeniedException("Access denied: You cannot view another patient's prescriptions");
            }
            patientId = selfPatientId;
        }

        List<Prescription> list;
        if (patientId != null) {
            list = prescriptionRepository.findByPatientId(patientId);
        } else if (doctorId != null) {
            list = prescriptionRepository.findByDoctorId(doctorId);
        } else if (status != null) {
            list = prescriptionRepository.findByStatus(status);
        } else {
            list = prescriptionRepository.findAll();
        }

        return list.stream()
                .map(PrescriptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PrescriptionResponse dispensePrescription(Long id, UserPrincipal principal) {
        Prescription rx = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + id));

        if (rx.getStatus() != PrescriptionStatus.PENDING) {
            throw new PrescriptionAlreadyDispensedException("Prescription is already " + rx.getStatus());
        }

        LocalDate today = LocalDate.now();

        // 1. Stock check: ensure all items have sufficient non-expired stock
        for (PrescriptionItem item : rx.getItems()) {
            List<Inventory> batches = inventoryRepository.findAvailableBatchesForDispense(item.getMedicine().getId(), today);
            int availableTotal = batches.stream().mapToInt(Inventory::getQuantityAvailable).sum();

            if (availableTotal < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for medicine '" + item.getMedicine().getName()
                        + "'. Required: " + item.getQuantity() + ", Available: " + availableTotal);
            }
        }

        // 2. FIFO Stock deduction (all items guaranteed to have sufficient stock)
        for (PrescriptionItem item : rx.getItems()) {
            List<Inventory> batches = inventoryRepository.findAvailableBatchesForDispense(item.getMedicine().getId(), today);
            int remainingNeeded = item.getQuantity();

            for (Inventory batch : batches) {
                int toDeduct = Math.min(batch.getQuantityAvailable(), remainingNeeded);
                batch.setQuantityAvailable(batch.getQuantityAvailable() - toDeduct);
                inventoryRepository.save(batch);
                remainingNeeded -= toDeduct;
                if (remainingNeeded == 0) {
                    break;
                }
            }
        }

        rx.setStatus(PrescriptionStatus.DISPENSED);
        rx.setDispensedAt(LocalDateTime.now());
        rx.setDispensedBy(principal != null ? principal.getUsername() : "SYSTEM");

        Prescription saved = prescriptionRepository.save(rx);
        return PrescriptionResponse.fromEntity(saved);
    }
}
