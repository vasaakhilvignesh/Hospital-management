package com.hospital.patientservice.service;

import com.hospital.patientservice.dto.MedicalHistoryRequest;
import com.hospital.patientservice.dto.MedicalHistoryResponse;
import com.hospital.patientservice.entity.MedicalHistory;
import com.hospital.patientservice.entity.Patient;
import com.hospital.patientservice.exception.MedicalHistoryNotFoundException;
import com.hospital.patientservice.exception.PatientNotFoundException;
import com.hospital.patientservice.repository.MedicalHistoryRepository;
import com.hospital.patientservice.repository.PatientRepository;
import com.hospital.patientservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;

    public MedicalHistoryService(MedicalHistoryRepository medicalHistoryRepository,
                                 PatientRepository patientRepository,
                                 PatientService patientService) {
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
    }

    @Transactional
    public MedicalHistoryResponse addMedicalHistory(Long patientId, MedicalHistoryRequest request, UserPrincipal principal) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + patientId));

        if ("PATIENT".equals(principal.getRole())) {
            throw new AccessDeniedException("Patients cannot add medical history records");
        }

        MedicalHistory history = new MedicalHistory(
                patient.getId(),
                request.getDiagnosis(),
                request.getTreatment(),
                request.getMedications(),
                request.getNotes(),
                request.getRecordDate(),
                request.getDoctorReference()
        );

        MedicalHistory saved = medicalHistoryRepository.save(history);
        return MedicalHistoryResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<MedicalHistoryResponse> getMedicalHistoryByPatient(Long patientId, UserPrincipal principal) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + patientId));

        patientService.verifyPatientAccess(patient, principal);

        return medicalHistoryRepository.findByPatientIdOrderByRecordDateDesc(patientId).stream()
                .map(MedicalHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedicalHistoryResponse updateMedicalHistory(Long patientId, Long historyId,
                                                      MedicalHistoryRequest request, UserPrincipal principal) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Patient not found with id: " + patientId);
        }

        if ("PATIENT".equals(principal.getRole())) {
            throw new AccessDeniedException("Patients cannot edit medical history");
        }

        MedicalHistory history = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new MedicalHistoryNotFoundException("Medical history record not found with id: " + historyId));

        history.setDiagnosis(request.getDiagnosis());
        history.setTreatment(request.getTreatment());
        history.setMedications(request.getMedications());
        history.setNotes(request.getNotes());
        if (request.getRecordDate() != null) {
            history.setRecordDate(request.getRecordDate());
        }
        if (request.getDoctorReference() != null) {
            history.setDoctorReference(request.getDoctorReference());
        }

        MedicalHistory updated = medicalHistoryRepository.save(history);
        return MedicalHistoryResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteMedicalHistory(Long patientId, Long historyId, UserPrincipal principal) {
        if (!"ADMIN".equals(principal.getRole())) {
            throw new AccessDeniedException("Only admins can delete medical history records");
        }
        if (!medicalHistoryRepository.existsById(historyId)) {
            throw new MedicalHistoryNotFoundException("Medical history not found with id: " + historyId);
        }
        medicalHistoryRepository.deleteById(historyId);
    }
}
