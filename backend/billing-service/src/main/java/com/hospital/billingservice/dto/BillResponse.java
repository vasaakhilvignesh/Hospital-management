package com.hospital.billingservice.dto;

import com.hospital.billingservice.entity.Bill;
import com.hospital.billingservice.entity.BillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BillResponse {

    private Long id;
    private String billNumber;
    private Long patientId;
    private Long appointmentId;
    private LocalDate billDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal insuranceAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private BillStatus status;
    private String notes;
    private List<BillItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BillResponse() {}

    public static BillResponse fromEntity(Bill bill) {
        BillResponse res = new BillResponse();
        res.setId(bill.getId());
        res.setBillNumber(bill.getBillNumber());
        res.setPatientId(bill.getPatientId());
        res.setAppointmentId(bill.getAppointmentId());
        res.setBillDate(bill.getBillDate());
        res.setSubtotal(bill.getSubtotal());
        res.setTaxAmount(bill.getTaxAmount());
        res.setDiscountAmount(bill.getDiscountAmount());
        res.setInsuranceAmount(bill.getInsuranceAmount());
        res.setTotalAmount(bill.getTotalAmount());
        res.setPaidAmount(bill.getPaidAmount());
        res.setBalanceAmount(bill.getBalanceAmount());
        res.setStatus(bill.getStatus());
        res.setNotes(bill.getNotes());
        res.setCreatedAt(bill.getCreatedAt());
        res.setUpdatedAt(bill.getUpdatedAt());

        if (bill.getItems() != null) {
            res.setItems(bill.getItems().stream()
                    .map(BillItemResponse::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            res.setItems(Collections.emptyList());
        }
        return res;
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

    public List<BillItemResponse> getItems() { return items; }
    public void setItems(List<BillItemResponse> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
