package com.hospital.patientservice.service;

import com.hospital.patientservice.dto.ImmunizationRequest;
import com.hospital.patientservice.dto.ImmunizationResponse;
import com.hospital.patientservice.entity.Immunization;
import com.hospital.patientservice.entity.Patient;
import com.hospital.patientservice.exception.ImmunizationNotFoundException;
import com.hospital.patientservice.exception.PatientNotFoundException;
import com.hospital.patientservice.repository.ImmunizationRepository;
import com.hospital.patientservice.repository.PatientRepository;
import com.hospital.patientservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImmunizationService {

    private final ImmunizationRepository immunizationRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;

    public ImmunizationService(ImmunizationRepository immunizationRepository,
                               PatientRepository patientRepository,
                               PatientService patientService) {
        this.immunizationRepository = immunizationRepository;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
    }

    @Transactional
    public ImmunizationResponse addImmunization(Long patientId, ImmunizationRequest request, UserPrincipal principal) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + patientId));

        if ("PATIENT".equals(principal.getRole())) {
            throw new AccessDeniedException("Patients cannot record immunization entries");
        }

        Immunization imm = new Immunization(
                patient.getId(),
                request.getVaccineName(),
                request.getDateGiven(),
                request.getNextDueDate(),
                request.getNotes()
        );

        Immunization saved = immunizationRepository.save(imm);
        return ImmunizationResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ImmunizationResponse> getImmunizationsByPatient(Long patientId, UserPrincipal principal) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + patientId));

        patientService.verifyPatientAccess(patient, principal);

        return immunizationRepository.findByPatientIdOrderByDateGivenDesc(patientId).stream()
                .map(ImmunizationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ImmunizationResponse updateImmunization(Long patientId, Long id, ImmunizationRequest request, UserPrincipal principal) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException("Patient not found with id: " + patientId);
        }

        if ("PATIENT".equals(principal.getRole())) {
            throw new AccessDeniedException("Patients cannot edit immunizations");
        }

        Immunization imm = immunizationRepository.findById(id)
                .orElseThrow(() -> new ImmunizationNotFoundException("Immunization record not found with id: " + id));

        imm.setVaccineName(request.getVaccineName());
        imm.setDateGiven(request.getDateGiven());
        imm.setNextDueDate(request.getNextDueDate());
        imm.setNotes(request.getNotes());

        Immunization updated = immunizationRepository.save(imm);
        return ImmunizationResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteImmunization(Long patientId, Long id, UserPrincipal principal) {
        if (!"ADMIN".equals(principal.getRole())) {
            throw new AccessDeniedException("Only admins can delete immunization records");
        }
        if (!immunizationRepository.existsById(id)) {
            throw new ImmunizationNotFoundException("Immunization record not found with id: " + id);
        }
        immunizationRepository.deleteById(id);
    }
}
