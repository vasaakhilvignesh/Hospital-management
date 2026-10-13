package com.hospital.billingservice.dto;

import com.hospital.billingservice.entity.Payment;
import com.hospital.billingservice.entity.PaymentMethod;
import com.hospital.billingservice.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private String paymentReference;
    private Long billId;
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionNote;
    private LocalDateTime createdAt;

    public PaymentResponse() {}

    public static PaymentResponse fromEntity(Payment p) {
        PaymentResponse res = new PaymentResponse();
        res.setId(p.getId());
        res.setPaymentReference(p.getPaymentReference());
        res.setBillId(p.getBillId());
        res.setPaymentDate(p.getPaymentDate());
        res.setAmount(p.getAmount());
        res.setPaymentMethod(p.getPaymentMethod());
        res.setStatus(p.getStatus());
        res.setTransactionNote(p.getTransactionNote());
        res.setCreatedAt(p.getCreatedAt());
        return res;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionNote() { return transactionNote; }
    public void setTransactionNote(String transactionNote) { this.transactionNote = transactionNote; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
