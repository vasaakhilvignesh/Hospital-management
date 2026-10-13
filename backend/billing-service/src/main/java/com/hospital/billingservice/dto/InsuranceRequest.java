package com.hospital.billingservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InsuranceRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotBlank(message = "Provider name is required")
    private String providerName;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotNull(message = "Coverage percentage is required")
    @DecimalMin(value = "0.0", message = "Coverage percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Coverage percentage cannot exceed 100%")
    private BigDecimal coveragePercentage;

    @NotNull(message = "Coverage limit is required")
    @DecimalMin(value = "0.0", message = "Coverage limit cannot be negative")
    private BigDecimal coverageLimit;

    @NotNull(message = "Valid from date is required")
    private LocalDate validFrom;

    @NotNull(message = "Valid until date is required")
    private LocalDate validUntil;

    public InsuranceRequest() {}

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

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
}
