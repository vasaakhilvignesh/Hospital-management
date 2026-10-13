package com.hospital.billingservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String billNumber;

    @Column(nullable = false)
    private Long patientId;

    private Long appointmentId;

    @Column(nullable = false)
    private LocalDate billDate = LocalDate.now();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal insuranceAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BillStatus status = BillStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<BillItem> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Bill() {}

    public Bill(String billNumber, Long patientId, Long appointmentId, LocalDate billDate,
                BigDecimal subtotal, BigDecimal taxAmount, BigDecimal discountAmount,
                BigDecimal insuranceAmount, BigDecimal totalAmount, BigDecimal balanceAmount,
                BillStatus status, String notes) {
        this.billNumber = billNumber;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.billDate = billDate != null ? billDate : LocalDate.now();
        this.subtotal = subtotal;
        this.taxAmount = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        this.insuranceAmount = insuranceAmount != null ? insuranceAmount : BigDecimal.ZERO;
        this.totalAmount = totalAmount;
        this.paidAmount = BigDecimal.ZERO;
        this.balanceAmount = balanceAmount;
        this.status = status != null ? status : BillStatus.PENDING;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(BillItem item) {
        items.add(item);
        item.setBill(this);
    }

    public void removeItem(BillItem item) {
        items.remove(item);
        item.setBill(null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getInsuranceAmount() { return insuranceAmount; }
    public void setInsuranceAmount(BigDecimal insuranceAmount) { this.insuranceAmount = insuranceAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
