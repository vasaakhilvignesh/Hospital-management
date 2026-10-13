package com.hospital.billingservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class BillRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long appointmentId;

    @NotEmpty(message = "Bill must contain at least one item")
    @Valid
    private List<BillItemRequest> items;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount cannot be negative")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax cannot be negative")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    private Boolean applyInsurance = true;

    private String notes;

    public BillRequest() {}

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public List<BillItemRequest> getItems() { return items; }
    public void setItems(List<BillItemRequest> items) { this.items = items; }

    public BigDecimal getDiscountAmount() { return discountAmount != null ? discountAmount : BigDecimal.ZERO; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTaxAmount() { return taxAmount != null ? taxAmount : BigDecimal.ZERO; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public Boolean getApplyInsurance() { return applyInsurance != null ? applyInsurance : true; }
    public void setApplyInsurance(Boolean applyInsurance) { this.applyInsurance = applyInsurance; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
