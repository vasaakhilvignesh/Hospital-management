package com.hospital.billingservice.service;

import com.hospital.billingservice.dto.PaymentRequest;
import com.hospital.billingservice.dto.PaymentResponse;
import com.hospital.billingservice.entity.Bill;
import com.hospital.billingservice.entity.BillStatus;
import com.hospital.billingservice.entity.Payment;
import com.hospital.billingservice.entity.PaymentStatus;
import com.hospital.billingservice.exception.BillNotFoundException;
import com.hospital.billingservice.exception.BillOperationException;
import com.hospital.billingservice.exception.InvalidPaymentException;
import com.hospital.billingservice.exception.PaymentNotFoundException;
import com.hospital.billingservice.repository.BillRepository;
import com.hospital.billingservice.repository.PaymentRepository;
import com.hospital.billingservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final com.hospital.billingservice.client.PatientServiceClient patientServiceClient;

    public PaymentService(PaymentRepository paymentRepository,
                          BillRepository billRepository,
                          com.hospital.billingservice.client.PatientServiceClient patientServiceClient) {
        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
        this.patientServiceClient = patientServiceClient;
    }

    public PaymentResponse recordPayment(PaymentRequest req, UserPrincipal principal) {
        Bill bill = billRepository.findById(req.getBillId())
                .orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + req.getBillId()));

        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new BillOperationException("Cannot record payment for a cancelled bill");
        }

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BillOperationException("Bill is already fully paid");
        }

        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Payment amount must be greater than zero");
        }

        if (req.getAmount().compareTo(bill.getBalanceAmount()) > 0) {
            throw new InvalidPaymentException("Payment amount of " + req.getAmount()
                    + " exceeds remaining balance of " + bill.getBalanceAmount());
        }

        // Patient isolation
        validatePatientAccess(bill.getPatientId(), principal);

        String paymentRef = "PAY-" + (System.currentTimeMillis() % 10000000);
        Payment payment = new Payment(
                paymentRef,
                bill.getId(),
                LocalDateTime.now(),
                req.getAmount(),
                req.getPaymentMethod(),
                PaymentStatus.SUCCESS,
                req.getTransactionNote()
        );

        BigDecimal newPaidAmount = bill.getPaidAmount().add(req.getAmount());
        BigDecimal newBalanceAmount = bill.getTotalAmount().subtract(newPaidAmount);
        bill.setPaidAmount(newPaidAmount);
        bill.setBalanceAmount(newBalanceAmount);

        if (newPaidAmount.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setStatus(BillStatus.PAID);
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        } else {
            bill.setStatus(BillStatus.PENDING);
        }

        billRepository.save(bill);
        Payment saved = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id, UserPrincipal principal) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));

        Bill bill = billRepository.findById(payment.getBillId())
                .orElseThrow(() -> new BillNotFoundException("Associated bill not found"));

        validatePatientAccess(bill.getPatientId(), principal);
        return PaymentResponse.fromEntity(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByBillId(Long billId, UserPrincipal principal) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + billId));

        validatePatientAccess(bill.getPatientId(), principal);
        return paymentRepository.findByBillId(billId).stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private void validatePatientAccess(Long patientId, UserPrincipal principal) {
        if (principal == null || !"PATIENT".equalsIgnoreCase(principal.getRole())) {
            return;
        }
        try {
            java.util.Map<String, Object> pat = patientServiceClient.getPatient(patientId, principal.getRawToken());
            Object uid = pat.get("userId");
            if (uid != null && uid.toString().equals(principal.getUserId().toString())) {
                return;
            }
        } catch (Exception ignored) {}
        throw new AccessDeniedException("Access denied: You cannot access another patient's payments");
    }

    public PaymentResponse refundPayment(Long id, UserPrincipal principal) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BillOperationException("Payment is already refunded");
        }

        Bill bill = billRepository.findById(payment.getBillId())
                .orElseThrow(() -> new BillNotFoundException("Associated bill not found"));

        payment.setStatus(PaymentStatus.REFUNDED);

        BigDecimal newPaidAmount = bill.getPaidAmount().subtract(payment.getAmount());
        if (newPaidAmount.compareTo(BigDecimal.ZERO) < 0) {
            newPaidAmount = BigDecimal.ZERO;
        }
        BigDecimal newBalanceAmount = bill.getTotalAmount().subtract(newPaidAmount);

        bill.setPaidAmount(newPaidAmount);
        bill.setBalanceAmount(newBalanceAmount);

        if (newPaidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            bill.setStatus(BillStatus.PENDING);
        } else if (newPaidAmount.compareTo(bill.getTotalAmount()) < 0) {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        } else {
            bill.setStatus(BillStatus.PAID);
        }

        billRepository.save(bill);
        Payment saved = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(saved);
    }
}
