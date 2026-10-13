package com.hospital.billingservice.config;

import com.hospital.billingservice.entity.*;
import com.hospital.billingservice.repository.BillRepository;
import com.hospital.billingservice.repository.InsuranceRepository;
import com.hospital.billingservice.repository.PaymentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class BillingDataSeeder implements CommandLineRunner {

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final InsuranceRepository insuranceRepository;

    public BillingDataSeeder(BillRepository billRepository,
                             PaymentRepository paymentRepository,
                             InsuranceRepository insuranceRepository) {
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.insuranceRepository = insuranceRepository;
    }

    @Override
    public void run(String... args) {
        if (billRepository.count() > 0) {
            return;
        }

        LocalDate today = LocalDate.now();

        // 1. Seed Insurance for Patient 1 (Peter Patient)
        Insurance ins = insuranceRepository.save(new Insurance(
                1L,
                "Medicare Blue Shield",
                "POL-MEDICARE-9921",
                new BigDecimal("80.00"), // 80% coverage
                new BigDecimal("5000.00"), // $5000 limit
                InsuranceStatus.ACTIVE,
                today.minusMonths(6),
                today.plusMonths(18)
        ));

        // 2. Seed Pending Bill (BILL-1001) for Patient 1
        // Subtotal: Consultation ($150) + Labs ($100) = $250.
        // Discount: $0. Total before insurance: $250.
        // Insurance 80% covered: $200. Total amount: $50. Paid: $0. Balance: $50. Status: PENDING.
        Bill pendingBill = new Bill(
                "BILL-1001",
                1L,
                1L,
                today.minusDays(2),
                new BigDecimal("250.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("200.00"),
                new BigDecimal("50.00"),
                new BigDecimal("50.00"),
                BillStatus.PENDING,
                "Outpatient Consultation and Lab Screening"
        );
        pendingBill.addItem(new BillItem(pendingBill, ItemType.CONSULTATION, "Doctor Consultation Fee", "DOC-1", 1, new BigDecimal("150.00"), new BigDecimal("150.00")));
        pendingBill.addItem(new BillItem(pendingBill, ItemType.LAB, "Blood Chemistry Panel", "LAB-BCP", 1, new BigDecimal("100.00"), new BigDecimal("100.00")));
        billRepository.save(pendingBill);

        // 3. Seed Partially Paid Bill (BILL-1002) for Patient 1
        // Subtotal: Specialist ($200) + Medicine ($100) = $300.
        // Insurance: $0. Total amount: $300.
        // Paid: $150. Balance: $150. Status: PARTIALLY_PAID.
        Bill partialBill = new Bill(
                "BILL-1002",
                1L,
                null,
                today.minusDays(5),
                new BigDecimal("300.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("300.00"),
                new BigDecimal("150.00"),
                BillStatus.PARTIALLY_PAID,
                "Specialist Follow-up and Medication"
        );
        partialBill.addItem(new BillItem(partialBill, ItemType.CONSULTATION, "Cardiology Consultation", "DOC-1", 1, new BigDecimal("200.00"), new BigDecimal("200.00")));
        partialBill.addItem(new BillItem(partialBill, ItemType.MEDICINE, "Prescription Dispensed", "RX-1", 1, new BigDecimal("100.00"), new BigDecimal("100.00")));
        partialBill.setPaidAmount(new BigDecimal("150.00"));
        Bill savedPartialBill = billRepository.save(partialBill);

        paymentRepository.save(new Payment(
                "PAY-8801",
                savedPartialBill.getId(),
                LocalDateTime.now().minusDays(3),
                new BigDecimal("150.00"),
                PaymentMethod.CARD,
                PaymentStatus.SUCCESS,
                "Initial 50% deposit via Visa"
        ));

        // 4. Seed Paid Bill (BILL-1003) for Patient 1
        // Subtotal: General Checkup ($120). Total amount: $120. Paid: $120. Balance: $0. Status: PAID.
        Bill paidBill = new Bill(
                "BILL-1003",
                1L,
                null,
                today.minusDays(10),
                new BigDecimal("120.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("120.00"),
                BigDecimal.ZERO,
                BillStatus.PAID,
                "Routine Annual Wellness Examination"
        );
        paidBill.addItem(new BillItem(paidBill, ItemType.CONSULTATION, "General Physician Checkup", "DOC-1", 1, new BigDecimal("120.00"), new BigDecimal("120.00")));
        paidBill.setPaidAmount(new BigDecimal("120.00"));
        Bill savedPaidBill = billRepository.save(paidBill);

        paymentRepository.save(new Payment(
                "PAY-8802",
                savedPaidBill.getId(),
                LocalDateTime.now().minusDays(10),
                new BigDecimal("120.00"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "Full settlement via Google Pay / UPI"
        ));
    }
}
