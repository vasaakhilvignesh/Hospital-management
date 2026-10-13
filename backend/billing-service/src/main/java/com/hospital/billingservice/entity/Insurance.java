package com.hospital.billingservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance")
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false, length = 100)
    private String providerName;

    @Column(nullable = false, unique = true, length = 50)
    private String policyNumber;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal coveragePercentage;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal coverageLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InsuranceStatus status = InsuranceStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validUntil;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Insurance() {}

    public Insurance(Long patientId, String providerName, String policyNumber,
                     BigDecimal coveragePercentage, BigDecimal coverageLimit,
                     InsuranceStatus status, LocalDate validFrom, LocalDate validUntil) {
        this.patientId = patientId;
        this.providerName = providerName;
        this.policyNumber = policyNumber;
        this.coveragePercentage = coveragePercentage;
        this.coverageLimit = coverageLimit;
        this.status = status != null ? status : InsuranceStatus.ACTIVE;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
