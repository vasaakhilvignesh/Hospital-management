package com.hospital.billingservice.service;

import com.hospital.billingservice.client.PatientServiceClient;
import com.hospital.billingservice.dto.InsuranceRequest;
import com.hospital.billingservice.dto.InsuranceResponse;
import com.hospital.billingservice.entity.Insurance;
import com.hospital.billingservice.entity.InsuranceStatus;
import com.hospital.billingservice.exception.InsuranceNotFoundException;
import com.hospital.billingservice.repository.InsuranceRepository;
import com.hospital.billingservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final PatientServiceClient patientServiceClient;

    public InsuranceService(InsuranceRepository insuranceRepository, PatientServiceClient patientServiceClient) {
        this.insuranceRepository = insuranceRepository;
        this.patientServiceClient = patientServiceClient;
    }

    public InsuranceResponse createInsurance(InsuranceRequest req, UserPrincipal principal) {
        if (principal != null && principal.getRawToken() != null) {
            patientServiceClient.getPatient(req.getPatientId(), principal.getRawToken());
        }

        Insurance insurance = new Insurance(
                req.getPatientId(),
                req.getProviderName(),
                req.getPolicyNumber(),
                req.getCoveragePercentage(),
                req.getCoverageLimit(),
                InsuranceStatus.ACTIVE,
                req.getValidFrom(),
                req.getValidUntil()
        );

        return InsuranceResponse.fromEntity(insuranceRepository.save(insurance));
    }

    @Transactional(readOnly = true)
    public InsuranceResponse getInsuranceById(Long id, UserPrincipal principal) {
        Insurance ins = insuranceRepository.findById(id)
                .orElseThrow(() -> new InsuranceNotFoundException("Insurance record not found with id: " + id));

        validatePatientAccess(ins.getPatientId(), principal);
        return InsuranceResponse.fromEntity(ins);
    }

    @Transactional(readOnly = true)
    public List<InsuranceResponse> getInsuranceByPatientId(Long patientId, UserPrincipal principal) {
        validatePatientAccess(patientId, principal);
        return insuranceRepository.findByPatientId(patientId).stream()
                .map(InsuranceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private void validatePatientAccess(Long patientId, UserPrincipal principal) {
        if (principal == null || !"PATIENT".equalsIgnoreCase(principal.getRole())) {
            return;
        }
        if (principal.getUserId().equals(patientId)) {
            return;
        }
        try {
            java.util.Map<String, Object> pat = patientServiceClient.getPatient(patientId, principal.getRawToken());
            Object uid = pat.get("userId");
            if (uid != null && uid.toString().equals(principal.getUserId().toString())) {
                return;
            }
        } catch (Exception ignored) {}
        throw new AccessDeniedException("Access denied: You can only view your own insurance");
    }

    public InsuranceResponse updateInsurance(Long id, InsuranceRequest req, UserPrincipal principal) {
        Insurance ins = insuranceRepository.findById(id)
                .orElseThrow(() -> new InsuranceNotFoundException("Insurance record not found with id: " + id));

        ins.setProviderName(req.getProviderName());
        ins.setPolicyNumber(req.getPolicyNumber());
        ins.setCoveragePercentage(req.getCoveragePercentage());
        ins.setCoverageLimit(req.getCoverageLimit());
        ins.setValidFrom(req.getValidFrom());
        ins.setValidUntil(req.getValidUntil());

        return InsuranceResponse.fromEntity(insuranceRepository.save(ins));
    }

    public InsuranceResponse deactivateInsurance(Long id, UserPrincipal principal) {
        Insurance ins = insuranceRepository.findById(id)
                .orElseThrow(() -> new InsuranceNotFoundException("Insurance record not found with id: " + id));

        ins.setStatus(InsuranceStatus.INACTIVE);
        return InsuranceResponse.fromEntity(insuranceRepository.save(ins));
    }
}
