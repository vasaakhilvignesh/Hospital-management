package com.hospital.billingservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String paymentReference;

    @Column(nullable = false)
    private Long billId;

    @Column(nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.SUCCESS;

    @Column(length = 255)
    private String transactionNote;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Payment() {}

    public Payment(String paymentReference, Long billId, LocalDateTime paymentDate,
                   BigDecimal amount, PaymentMethod paymentMethod, PaymentStatus status,
                   String transactionNote) {
        this.paymentReference = paymentReference;
        this.billId = billId;
        this.paymentDate = paymentDate != null ? paymentDate : LocalDateTime.now();
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status != null ? status : PaymentStatus.SUCCESS;
        this.transactionNote = transactionNote;
        this.createdAt = LocalDateTime.now();
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
