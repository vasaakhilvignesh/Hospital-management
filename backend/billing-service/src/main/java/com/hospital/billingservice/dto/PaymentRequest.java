package com.hospital.billingservice.dto;

import com.hospital.billingservice.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull(message = "Bill ID is required")
    private Long billId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String transactionNote;

    public PaymentRequest() {}

    public PaymentRequest(Long billId, BigDecimal amount, PaymentMethod paymentMethod, String transactionNote) {
        this.billId = billId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionNote = transactionNote;
    }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionNote() { return transactionNote; }
    public void setTransactionNote(String transactionNote) { this.transactionNote = transactionNote; }
}
