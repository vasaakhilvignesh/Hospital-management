package com.hospital.patientservice.service;

import com.hospital.patientservice.dto.PatientRequest;
import com.hospital.patientservice.dto.PatientResponse;
import com.hospital.patientservice.entity.Patient;
import com.hospital.patientservice.exception.DuplicatePatientException;
import com.hospital.patientservice.exception.PatientNotFoundException;
import com.hospital.patientservice.repository.PatientRepository;
import com.hospital.patientservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import com.hospital.patientservice.client.AuthServiceClient;
import com.hospital.patientservice.dto.PatientRegistrationRequest;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AuthServiceClient authServiceClient;

    public PatientService(PatientRepository patientRepository, AuthServiceClient authServiceClient) {
        this.patientRepository = patientRepository;
        this.authServiceClient = authServiceClient;
    }

    @Transactional
    public PatientResponse registerPatient(PatientRegistrationRequest request) {
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicatePatientException("Patient with email '" + request.getEmail() + "' already exists");
        }

        // 1. Create account in Auth Service with Role PATIENT
        Map<String, Object> authReq = new HashMap<>();
        authReq.put("username", request.getUsername());
        authReq.put("password", request.getPassword());
        authReq.put("fullName", request.getFirstName() + " " + request.getLastName());
        authReq.put("email", request.getEmail());
        authReq.put("phone", request.getPhone());
        authReq.put("role", "PATIENT");

        Map<String, Object> authUser;
        try {
            authUser = authServiceClient.registerUser(authReq);
        } catch (Exception e) {
            throw new RuntimeException("Account creation in Auth Service failed: " + e.getMessage(), e);
        }

        Long userId = null;
        if (authUser != null && authUser.get("id") != null) {
            userId = Long.valueOf(authUser.get("id").toString());
        }

        // 2. Persist Patient entity with linked userId
        String code = "PAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Patient patient = new Patient(
                code,
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getPhone(),
                request.getEmail(),
                request.getAddress(),
                request.getBloodGroup(),
                request.getEmergencyContact()
        );

        Patient saved;
        try {
            saved = patientRepository.save(patient);
        } catch (Exception ex) {
            // Rollback auth user creation if patient persistence fails
            if (userId != null) {
                authServiceClient.deleteUser(userId);
            }
            throw ex;
        }

        return PatientResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new PatientNotFoundException("Patient record not found for user id: " + userId));
        return PatientResponse.fromEntity(patient);
    }

    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank() && patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicatePatientException("Patient with email '" + request.getEmail() + "' already exists");
        }

        String code = request.getPatientCode();
        if (code == null || code.isBlank()) {
            code = "PAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else if (patientRepository.existsByPatientCode(code)) {
            throw new DuplicatePatientException("Patient code '" + code + "' already exists");
        }

        Patient patient = new Patient(
                code,
                request.getUserId(),
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getPhone(),
                request.getEmail(),
                request.getAddress(),
                request.getBloodGroup(),
                request.getEmergencyContact()
        );

        Patient saved = patientRepository.save(patient);
        return PatientResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients(UserPrincipal principal) {
        if ("PATIENT".equals(principal.getRole())) {
            // Patient user can only view their own record
            return patientRepository.findByUserId(principal.getUserId())
                    .map(p -> List.of(PatientResponse.fromEntity(p)))
                    .orElseGet(List::of);
        }
        return patientRepository.findAll().stream()
                .map(PatientResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id, UserPrincipal principal) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        verifyPatientAccess(patient, principal);
        return PatientResponse.fromEntity(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientByCode(String code, UserPrincipal principal) {
        Patient patient = patientRepository.findByPatientCode(code)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with code: " + code));

        verifyPatientAccess(patient, principal);
        return PatientResponse.fromEntity(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(String query, UserPrincipal principal) {
        if ("PATIENT".equals(principal.getRole())) {
            throw new AccessDeniedException("Patients are not authorized to perform general patient searches");
        }
        return patientRepository.searchPatients(query).stream()
                .map(PatientResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public PatientResponse updatePatient(Long id, PatientRequest request, UserPrincipal principal) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        if ("PATIENT".equals(principal.getRole())) {
            throw new AccessDeniedException("Patients cannot directly update clinical profile data");
        }

        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setEmergencyContact(request.getEmergencyContact());

        Patient updated = patientRepository.save(patient);
        return PatientResponse.fromEntity(updated);
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    public void verifyPatientAccess(Patient patient, UserPrincipal principal) {
        if ("PATIENT".equals(principal.getRole())) {
            if (patient.getUserId() == null || !patient.getUserId().equals(principal.getUserId())) {
                throw new AccessDeniedException("Patients can only access their own records");
            }
        }
    }
}
