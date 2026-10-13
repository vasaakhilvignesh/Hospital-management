package com.hospital.billingservice.dto;

import com.hospital.billingservice.entity.Insurance;
import com.hospital.billingservice.entity.InsuranceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InsuranceResponse {

    private Long id;
    private Long patientId;
    private String providerName;
    private String policyNumber;
    private BigDecimal coveragePercentage;
    private BigDecimal coverageLimit;
    private InsuranceStatus status;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InsuranceResponse() {}

    public static InsuranceResponse fromEntity(Insurance ins) {
        InsuranceResponse res = new InsuranceResponse();
        res.setId(ins.getId());
        res.setPatientId(ins.getPatientId());
        res.setProviderName(ins.getProviderName());
        res.setPolicyNumber(ins.getPolicyNumber());
        res.setCoveragePercentage(ins.getCoveragePercentage());
        res.setCoverageLimit(ins.getCoverageLimit());
        res.setStatus(ins.getStatus());
        res.setValidFrom(ins.getValidFrom());
        res.setValidUntil(ins.getValidUntil());
        res.setCreatedAt(ins.getCreatedAt());
        res.setUpdatedAt(ins.getUpdatedAt());
        return res;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public BigDecimal getCoveragePercentage() { return coveragePercentage; }
    public void setCoveragePercentage(BigDecimal coveragePercentage) { this.coveragePercentage = coveragePercentage; }

    public BigDecimal getCoverageLimit() { return coverageLimit; }
    public void setCoverageLimit(BigDecimal coverageLimit) { this.coverageLimit = coverageLimit; }

    public InsuranceStatus getStatus() { return status; }
    public void setStatus(InsuranceStatus status) { this.status = status; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
